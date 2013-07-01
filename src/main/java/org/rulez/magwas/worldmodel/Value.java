package org.rulez.magwas.worldmodel;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.Query;
import org.hibernate.Session;

@Entity
@Table(name = "value")
public class Value implements Serializable {
    
    private static final long         serialVersionUID = 1L;
    
    private static Map<String, Value> cache            = new HashMap<String, Value>();
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column
    private int                       id;
    
    @Column(unique = true)
    private String                    value;
    
    public static Value getValueByValue(String value, Session session) {
        if ((value == null) || value.equals("")) {
            return null;
        }
        if (cache.containsKey(value)) {
            return cache.get(value);
        }
        Query query = session.createQuery("from Value where value = :value");
        query.setParameter("value", value);
        List<?> l = query.list();
        Value obj;
        if (l.isEmpty()) {
            obj = new Value();
            obj.setValue(value);
            cache.put(value, obj);
            session.save(obj);
        } else {
            obj = (Value) query.list().get(0);
            
        }
        return obj;
    }
    
    public String getValue() {
        return value;
    }
    
    public void setValue(String value) {
        this.value = value;
    }
    
    public int getId() {
        return id;
    }
    
}
