package net.emeri.practice.player.value;

import org.apache.commons.lang3.Validate;
import java.io.Serializable;

/**
 * Created by Matthew E on 6/12/2017.
 */
public class Value<O> implements Cloneable, Serializable {

    private O value;
    private ValueType valueType;


    public Value(O value, ValueType valueType) {
        this.value = Validate.notNull(value);
        this.valueType = Validate.notNull(valueType);
    }

    public O getValue() {
        return value;
    }

    public ValueType getValueType() {
        return valueType;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return new Value<>(value, valueType);
    }

}
