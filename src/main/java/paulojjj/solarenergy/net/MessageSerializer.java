package paulojjj.solarenergy.net;

import java.lang.reflect.Field;
import java.nio.charset.Charset;

import io.netty.buffer.ByteBuf;

public class MessageSerializer {

	private static final Charset CHARSET = Charset.forName("UTF-8");

	protected void write(Object object, Field field, ByteBuf buf) {
		if(!field.isAccessible()) {
			field.setAccessible(true);
		}
		Class<?> type = field.getType();
		Object value;
		try {
			value = field.get(object);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new RuntimeException("Error writing field", e);
		}
		if(type == Boolean.TYPE) {
			buf.writeBoolean((boolean)value);
		}
		else if(type == Byte.TYPE) {
			buf.writeByte((byte)value);
		}
		else if(type == Short.TYPE) {
			buf.writeShort((short)value);
		}
		else if(type == Integer.TYPE) {
			buf.writeInt((int)value);
		}
		else if(type == Long.TYPE) {
			buf.writeLong((long)value);
		}
		else if(type == Float.TYPE) {
			buf.writeFloat((float)value);
		}
		else if(type == Double.TYPE) {
			buf.writeDouble((double)value);
		}
		else if(CharSequence.class.isAssignableFrom(type)) {
			CharSequence str = (CharSequence)value;
			buf.writeShort((short)value);
			buf.writeCharSequence(str, CHARSET);
		}
		else {
			throw new UnsupportedOperationException("Field type " + type + " not supported");
		}
	}

	protected void read(Object object, Field field, ByteBuf buf) {
		if(!field.isAccessible()) {
			field.setAccessible(true);
		}
		Class<?> type = field.getType();
		Object value = null;
		if(type == Boolean.TYPE) {
			value = buf.readBoolean();
		}
		else if(type == Byte.TYPE) {
			value = buf.readByte();
		}
		else if(type == Short.TYPE) {
			value = buf.readShort();
		}
		else if(type == Integer.TYPE) {
			value = buf.readInt();
		}
		else if(type == Long.TYPE) {
			value = buf.readLong();
		}
		else if(type == Float.TYPE) {
			value = buf.readFloat();
		}
		else if(type == Double.TYPE) {
			value = buf.readDouble();
		}
		else if(CharSequence.class.isAssignableFrom(type)) {
			short length = buf.readShort();
			value = buf.readCharSequence(length, CHARSET);
		}
		else {
			throw new UnsupportedOperationException("Field type " + type + " not supported");
		}

		try {
			field.set(object, value);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new RuntimeException("Error reading field" , e);
		}
	}

	public void write(Object message, ByteBuf buf) {
		Field[] fields = message.getClass().getDeclaredFields();
		for(Field field : fields) {
			write(message, field, buf);
		}
	}

	public <T> T read(Class<T> messageClass, ByteBuf buf) {
		T message;
		try {
			message = messageClass.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException("Exception creating message instance");
		}
		Field[] fields = messageClass.getDeclaredFields();
		for(Field field : fields) {
			read(message, field, buf);
		}
		return message;
	}

}
