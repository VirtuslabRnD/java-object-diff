package de.danielbechler.diff.identity;

import de.danielbechler.util.Objects;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;


/**
 * A strategy that considers two objects as having the same identity 
 * when they hava matching values in a given field.
 * 
 * Due to the type erasure that happens at run-time, the type of the objects is not checked at all.
 * The only thing that matters is that both of them have a field or a getter method with the same name.
 * 
 * For example, a {@code FieldIdentityStrategy("foo")} will compare the values 
 * of the public field {@code foo} or the public method {@code getFoo()}.
 * If the object has both a public field and a public getter, the field has the priority.
 */
public class FieldIdentityStrategy implements IdentityStrategy {
    private final String fieldName;

    public FieldIdentityStrategy(String fieldName)
    {
        this.fieldName = fieldName;
    }

    public String getFieldName()
    {
        return fieldName;
    }

    private Optional<Field> getField(Object obj)
    {
        for (Field f : obj.getClass().getFields())
        {
            if (fieldName.equals(f.getName()))
            {
                return Optional.of(f);
            }
        }
        return Optional.empty();
    }

    private Object getFieldValue(Object obj)
    {
        Optional<Field> field = getField(obj);
        try
        {
            return field.get().get(obj);
        }
        catch (IllegalAccessException e)
        {
            throw new RuntimeException(e);
        }
    }

    private Optional<Method> getGetter(Object obj)
    {
        String getterName = "get" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
        for (Method m : obj.getClass().getMethods())
        {
            if (m.getParameterCount() == 0 && getterName.equals(m.getName()))
            {
                return Optional.of(m);
            }
        }
        return Optional.empty();
    }

    private Object getGetterValue(Object obj)
    {
        Optional<Method> getter = getGetter(obj);
        try
        {
            return getter.get().invoke(obj);
        }
        catch (InvocationTargetException e)
        {
            throw new RuntimeException(e);
        }
        catch (IllegalAccessException e)
        {
            throw new RuntimeException(e);
        }
    }

    public Object getValue(Object obj)
    {
        if (getField(obj).isPresent()) return getFieldValue(obj);
        if (getGetter(obj).isPresent()) return getGetterValue(obj);
        String msg = "The object doesn't have a visible field or getter for the field named " + fieldName;
        throw new IllegalArgumentException(msg);
    }

    public boolean equals(final Object working, final Object base) {
        if (working == null || base == null)
        {
            return false;
        }
        if (getField(working).isPresent() || getField(base).isPresent())
        {
            return Objects.isEqual(getFieldValue(working), getFieldValue(base));
        }
        if (getGetter(working).isPresent() || getGetter(base).isPresent())
        {
            return Objects.isEqual(getGetterValue(working), getGetterValue(base));
        }
        return false;
    }

}
