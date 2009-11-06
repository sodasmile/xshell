package com.sodasmile.xshell.args;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Array;
import java.util.*;

/**
 * @author <a href="mailto:runepeter@gmail.com">Rune Peter Bj&oslash;rnstad</a>
 */
public class Options {

    public static String usage( final Class<?> clazz ) {

        StringBuffer buf = new StringBuffer();

        return null;
    }

    public static void apply( final Object instance, final String[] arguments ) {

        Map<String, String> map = new HashMap<String, String>();

        for ( int i = 0; i < arguments.length; i++ ) {

            String argument = arguments[i];

            if ( argument.startsWith( "-" ) ) {

                List<String> list = new ArrayList<String>();
                while ( ( i < arguments.length - 1 ) && !arguments[i + 1].startsWith( "-" ) ) {
                    list.add( arguments[++i] );
                }

                String value = join( list, ' ' );
                map.put( argument, value );
            }

        }

        Field[] fields = instance.getClass().getDeclaredFields();
        for ( Field field : fields ) {

            if ( field.isAnnotationPresent( Option.class ) ) {

                Option option = field.getAnnotation( Option.class );

                String key = "-" + option.name();
                if ( option.required() && !map.containsKey( key ) ) {
                    throw new IllegalArgumentException( "Required option '" + key + "' not provided" );
                }


                String value = map.get( "-" + option.name() );

                if (!option.required() && value == null) {
                    continue;
                }
                
                try {

                    Class<?> fieldType = field.getType();

                    Object _value;

                    if ( fieldType == Boolean.TYPE || fieldType == Boolean.class ) {

                        _value = value != null;

                    } else if ( fieldType.isEnum() ) {

                        Class<Enum> enumClass = (Class<Enum>) fieldType;

                        try {
                            _value = Enum.valueOf( enumClass, value );
                        } catch ( Exception e ) {
                            throw new RuntimeException( "Cannot set value of enum-field '" + field.getName() + "' to value '" + value + "'.", e );
                        }

                    } else if ( fieldType.isArray() ) {

                        String delimiter = "" + option.delimiter();

                        String[] array = value.split( delimiter );

                        Class<?> type = fieldType.getComponentType();
                        Object jalla = Array.newInstance( type, array.length );

                        for ( int i = 0; i < array.length; i++ ) {
                            Array.set( jalla, i, convert(array[i], type) );
                        }
                        
                        _value = jalla;

                    } else {

                        _value = value;

                    }

                    field.setAccessible( true );
                    field.set( instance, _value );

                } catch ( IllegalAccessException e ) {
                    throw new RuntimeException( "Unable to set field " + field.getName() + "'s value to '" + value + "'." );
                }

            }

        }

    }

    private static Object convert(final String value, final Class targetClass) {

        if (targetClass.isPrimitive()) {

            String className = targetClass.getName();
            if ("int".equals( className )) {
                return Integer.parseInt( value.trim() );
            }

        }

        return value;
    }

    private static String join( List<String> values, char delimiter ) {
        StringBuffer buf = new StringBuffer();
        for ( Iterator<String> it = values.iterator(); it.hasNext(); ) {
            buf.append( it.next().trim() );
            if ( it.hasNext() ) {
                buf.append( delimiter );
            }
        }

        return buf.toString();
    }

}
