/**
 * Copyright 2012 Ronen Hamias, Anton Kharenko
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.socketio.netty.pipeline;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.socketio.netty.ISession;
import org.socketio.netty.ISocketIOListener;
import org.socketio.netty.packets.ConnectPacket;
import org.socketio.netty.packets.IPacket;
import org.socketio.netty.packets.Packet;
import org.socketio.netty.packets.PacketType;
import org.socketio.netty.session.IManagedSession;
import org.socketio.netty.session.ISessionDisconnectHandler;
import org.socketio.netty.storage.SessionStorage;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

@ChannelHandler.Sharable
public class PacketDispatcherHandler extends ChannelInboundHandlerAdapter implements ISessionDisconnectHandler {

	private final Logger log = LoggerFactory.getLogger(getClass());

	private final SessionStorage sessionStorage;

	private final ISocketIOListener listener;

	public PacketDispatcherHandler(SessionStorage sessionStorage, ISocketIOListener listener) {
		this.sessionStorage = sessionStorage;
		this.listener = listener;
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		log.debug("Channel active: {}", ctx.channel());
		super.channelActive(ctx);
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		log.debug("Channel inactive: {}", ctx.channel());
		super.channelInactive(ctx);
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object message) throws Exception {
		final Channel channel = ctx.channel();
		if (message instanceof IPacket) {
			final IPacket packet = (IPacket) message;
			try {
				log.debug("Dispatching packet: {} from channel: {}", packet, channel);
				dispatchPacket(channel, packet);
			} catch (Exception e) {
				log.error("Failed to dispatch packet: {}", packet, e);
			}
		} else {
			log.warn("Received unknown message: {} from channel {}", message, channel);
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		log.error("Exception caught at channel: {}, {}", ctx.channel(), cause);
	}

	private void dispatchPacket(final Channel channel, final IPacket packet) throws Exception {
		if (packet instanceof ConnectPacket) {
			ConnectPacket connectPacket = (ConnectPacket) packet;
			final IManagedSession session = sessionStorage.getSession(connectPacket, channel, this);
			onConnectPacket(channel, session);
		} else if (packet instanceof Packet) {
			Packet message = (Packet) packet;
			final String sessionId = packet.getSessionId();
			final IManagedSession session = sessionStorage.getSessionIfExist(sessionId);
			if (session != null) {
				onPacket(channel, session, message);
			}
		} else {
			throw new UnsupportedPacketTypeException(packet);
		}
	}

	private void onConnectPacket(final Channel channel, final IManagedSession session) {
		boolean initialConnect = session.connect(channel);
		if (initialConnect && listener != null) {
			listener.onConnect(session);
		}
	}

	private void onPacket(final Channel channel, final IManagedSession session, final Packet packet) {
		if (packet.getType() == PacketType.DISCONNECT) {
			log.debug("Got {} packet, {} session will be disconnected", packet.getType().name(), session.getSessionId());
			session.disconnect(channel);
		} else {
			session.acceptPacket(channel, packet);
			if (listener != null) {
				if (packet.getType() == PacketType.MESSAGE) {
					listener.onMessage(session, packet.getData().toString());
				} else if (packet.getType() == PacketType.JSON) {
					listener.onJsonObject(session, packet.getData());
				}
			}
		}
	}

	@Override
	public void onSessionDisconnect(ISession session) {
		if (sessionStorage.containSession(session.getSessionId())) {
			log.debug("Client with sessionId: {} disconnected", session.getSessionId());
			sessionStorage.removeSession(session.getSessionId());
			if (listener != null) {
				listener.onDisconnect(session);
			}
		}
	}

}
