package paulojjj.solarenergy.net;

public interface IMessageListener<T> {
	
	void onMessage(T message);

}
