package gov.cida.sedmap.io.util;

import java.lang.reflect.Field;

public class ReflectUtil {

	public static Object getDeclaredFieldValue(String fieldname, Object obj) {
		Object value = null;

		try {
			Field field = getDeclaredField("children", obj.getClass());
			field.setAccessible(true);
			value = field.get(obj);

		} catch (Exception e) {
			// TODO ya ya, I know I should do something here
			e.printStackTrace();
		}

		return value;
	}

	public static Field getDeclaredField(String name, Class<?> clas) {
		Field field = null;

		try {
			field = clas.getDeclaredField("children");
		} catch (NoSuchFieldException e) {
			if ( clas.equals(Object.class) ) {
				// no where else to look
				return field;
			}
			return getDeclaredField(name,clas.getSuperclass());
		} catch (Exception e) {
			e.printStackTrace();
		}

		return field;
	}


}
