package paulojjj.solarenergy;

import io.netty.buffer.ByteBuf;

public interface INetworkSerializable {
	
	void writeTo(ByteBuf stream);
	
	void readFrom(ByteBuf stream);

}
