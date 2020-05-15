package paulojjj.solarenergy.net;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.netty.buffer.ByteBuf;

public class MessageSerializer {

	private static final Charset CHARSET = Charset.forName("UTF-8");

	public static Class<?> getGenericClass(Collection<?> collection, Type genericType) {
		if(genericType instanceof ParameterizedType) {
			ParameterizedType pType = (ParameterizedType)genericType;
			return (Class<?>)pType.getActualTypeArguments()[0];
		}
		throw new RuntimeException("Could not get generic class");
	}

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
		write(buf, type, value, field.getGenericType());
	}

	private void write(ByteBuf buf, Class<?> type, Object value, Type genericType) {
		if(type == Boolean.TYPE || type.equals(Boolean.class)) {
			buf.writeBoolean((boolean)value);
		}
		else if(type == Byte.TYPE || type.equals(Byte.class)) {
			buf.writeByte((byte)value);
		}
		else if(type == Short.TYPE || type.equals(Short.class)) {
			buf.writeShort((short)value);
		}
		else if(type == Integer.TYPE || type.equals(Integer.class)) {
			buf.writeInt((int)value);
		}
		else if(type == Long.TYPE || type.equals(Long.class)) {
			buf.writeLong((long)value);
		}
		else if(type == Float.TYPE || type.equals(Float.class)) {
			buf.writeFloat((float)value);
		}
		else if(type == Double.TYPE || type.equals(Double.class)) {
			buf.writeDouble((double)value);
		}
		else if(CharSequence.class.isAssignableFrom(type)) {
			CharSequence str = (CharSequence)value;
			buf.writeShort((short)value);
			buf.writeCharSequence(str, CHARSET);
		}
		else if(Collection.class.isAssignableFrom(type)) {
			@SuppressWarnings("unchecked")
			Collection<Object> collection = (Collection<Object>)value;
			Class<?> collectionClass = getGenericClass(collection, genericType);
			write(buf, Integer.class, collection.size(), Integer.class);
			for(Object v : collection) {
				write(buf, collectionClass, v, collectionClass);
			}
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
		Object value = read(buf, type, field.getGenericType());

		try {
			field.set(object, value);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new RuntimeException("Error reading field" , e);
		}
	}

	@SuppressWarnings("unchecked")
	private Object read(ByteBuf buf, Class<?> type, Type genericType) {
		Object value = null;		
		if(type == Boolean.TYPE || type.equals(Boolean.class)) {
			value = buf.readBoolean();
		}
		else if(type == Byte.TYPE || type.equals(Byte.class)) {
			value = buf.readByte();
		}
		else if(type == Short.TYPE || type.equals(Short.class)) {
			value = buf.readShort();
		}
		else if(type == Integer.TYPE || type.equals(Integer.class)) {
			value = buf.readInt();
		}
		else if(type == Long.TYPE || type.equals(Long.class)) {
			value = buf.readLong();
		}
		else if(type == Float.TYPE || type.equals(Float.class)) {
			value = buf.readFloat();
		}
		else if(type == Double.TYPE || type.equals(Double.class)) {
			value = buf.readDouble();
		}
		else if(CharSequence.class.isAssignableFrom(type)) {
			short length = buf.readShort();
			value = buf.readCharSequence(length, CHARSET);
		}
		else if(Collection.class.isAssignableFrom(type)) {
			Collection<Object> collection = (Collection<Object>)value;
			Class<?> collectionMemberClass = getGenericClass(collection, genericType);
			if(!type.isInterface()) {
				try {
					value = type.newInstance();
				} catch (InstantiationException | IllegalAccessException e) {
					throw new RuntimeException(e);
				}
			}
			else {
				if(Set.class.isAssignableFrom(type)) {
					value = new HashSet<>();
				}
				else if(List.class.isAssignableFrom(type) || Collection.class.isAssignableFrom(type)) {
					value = new ArrayList<>();
				}
				else {
					throw new RuntimeException("Collection type not supported " + type);
				}
				
			}

			int size = (Integer)read(buf, Integer.class, Integer.class);
			for(int i=0; i<size; i++) {
				Object memberValue = read(buf, collectionMemberClass, collectionMemberClass);
				((Collection<Object>)value).add(memberValue);
			}
		}
		else {
			throw new UnsupportedOperationException("Field type " + type + " not supported");
		}
		return value;
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
