package net.sourceforge.subsonic.taglib;


import java.util.List;

import net.sourceforge.subsonic.domain.Album;

import org.apache.commons.lang.StringEscapeUtils;


public class SubsonicFunctions {

	public static String esc(Object object) {
		if(object == null) {
			return "[]";
		}
		
		if(List.class.isAssignableFrom(object.getClass())) {
			StringBuilder result = new StringBuilder();
			result.append("[");
			List objectList = (List) object;
			for(int i = 0; i < objectList.size(); i++) {
				Object obj = objectList.get(i);
				result.append(esc(obj));
				if(i < objectList.size() - 1) {
					result.append(',');
				}
			}
			result.append("]");
			return result.toString();
		}
		
		if(object.getClass().isArray()) {
			StringBuilder result = new StringBuilder();
			result.append("[");
			Object[] objectArray = (Object[]) object;
			for(int i = 0; i < objectArray.length; i++) {
				Object obj = objectArray[i];
				result.append(esc(obj));
				if(i < objectArray.length - 1) {
					result.append(',');
				}
			}
			result.append("]");
			return result.toString();
		}
		
		if(Album.class.isAssignableFrom(object.getClass())) {
			return esc(((Album)object).getTrackUris());
		}
		
		return "'" + StringEscapeUtils.escapeJavaScript(object.toString()) + "'";
	}
	
	public static void main(String... args) {
		String[] foo = new String[] {"a","b","c"};
		System.out.println(esc(foo));
		
	}
}
