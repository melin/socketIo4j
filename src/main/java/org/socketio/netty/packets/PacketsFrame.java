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
package org.socketio.netty.packets;

import java.util.LinkedList;
import java.util.List;


public class PacketsFrame extends AbstractPacket {

	private List<IPacket> packets = new LinkedList<IPacket>();

	public PacketsFrame() {
	}

	public List<IPacket> getPackets() {
		return packets;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("PacketsFrame [");
		builder.append(super.toString());
		builder.append(", packets=");
		builder.append(packets);
		builder.append("]");
		return builder.toString();
	}
	
}
