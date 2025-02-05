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
package org.socketio.netty;


/**
 * An empty implementation of the ISocketIOListener interface, provided as a convenience 
 * to simplify the task of creating listeners, by extending and implementing only the 
 * methods of interest.
 * 
 * @author Anton Kharenko
 * 
 */
public class SocketIOAdapter implements ISocketIOListener {

	@Override
	public void onConnect(ISession session) {
	}

	@Override
	public void onJsonObject(ISession session, Object obj) {
	}

	@Override
	public void onMessage(ISession session, String message) {
	}

	@Override
	public void onDisconnect(ISession session) {
	}

}
