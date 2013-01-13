package restful.server.dao;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.persistence.Embedded;
import javax.persistence.Transient;

import restful.server.domain.Category;

import com.google.appengine.api.datastore.EntityNotFoundException;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.Query;
import com.googlecode.objectify.util.DAOBase;

/**
 * Generic DAO for use with Objectify
 * 
 * @author turbomanage
 * 
 * @param <T>
 */
public abstract class ObjectifyDao<T> extends DAOBase
{

  static final int BAD_MODIFIERS = Modifier.FINAL | Modifier.STATIC
  | Modifier.TRANSIENT;


  protected Class<T> clazz;

  @SuppressWarnings("unchecked")
  public ObjectifyDao(Class<T> clazz)
  {
	  this.clazz = clazz;
	  /*
    clazz = (Class<T>) ((ParameterizedType) getClass()
        .getGenericSuperclass()).getActualTypeArguments()[0];
        */
  }

  public Key<T> put(T entity)

  {
    return ofy().put(entity);
  }

  public Map<Key<T>, T> putAll(Iterable<T> entities)
  {
    return ofy().put(entities);
  }

  public void delete(T entity)
  {
    ofy().delete(entity);
  }

  public void deleteKey(Key<T> entityKey)
  {
    ofy().delete(entityKey);
  }

  public void deleteAll(Iterable<T> entities)
  {
    ofy().delete(entities);
  }

  public void deleteKeys(Iterable<Key<T>> keys)
  {
    ofy().delete(keys);
  }

  public T get(Long id) throws EntityNotFoundException
  {
    return ofy().get(this.clazz, id);
    
  }

  public T get(Key<T> key) throws EntityNotFoundException
  {
    return ofy().get(key);
  }

  public Map<Key<T>, T> get(Iterable<Key<T>> keys)
  {
    return ofy().get(keys);
  }

  public List<T> getByRange(int start, int length)
  {
    Query<T> q = ofy().query(clazz).offset(start).limit(length);
    return q.list();
  }
  
  public boolean isFound(Long id) {
	T object = ofy().find(clazz, id);
	if(object == null) return false;
	else return true;
  }
  
  public int countAll()
  {
    return ofy().query(clazz).count();
  }

   public List<T> getByProperty(String propName, Object propValue)
   {
     Query<T> q = ofy().query(clazz);
     q.filter(propName, propValue);
     return q.list();
   }

   /*
   public List<Key<T>> getKeysByProperty(String propName, Object propValue)
   {
     Query<T> q = ofy().query(clazz);
     q.filter(propName, propValue);
     return q.listKeys();
   }
   */
   public List<T> getByExample(T exampleObj)
   {
     Query<T> queryByExample = buildQueryByExample(exampleObj);
     return queryByExample.list();
   }

   public Key<T> getKey(Long id)
   {
     return new Key<T>(this.clazz, id);
   }

   public Key<T> key(T obj)
   {
     return ObjectifyService.factory().getKey(obj);
   }

   public List<T> getChildren(Object parent)
   {
     return ofy().query(clazz).ancestor(parent).list();
   }

   public List<Key<T>> getChildKeys(Object parent)
   {
     return ofy().query(clazz).ancestor(parent).listKeys();
   }

   protected Query<T> buildQueryByExample(T exampleObj)
   {
     Query<T> q = ofy().query(clazz);

     // Add all non-null properties to query filter
     for (Field field : clazz.getDeclaredFields())
     {
       // Ignore transient, embedded, array, and collection properties
       if (field.isAnnotationPresent(Transient.class)
           || (field.isAnnotationPresent(Embedded.class))
           || (field.getType().isArray())
           || (field.getType().isArray())
           || (Collection.class.isAssignableFrom(field.getType()))
           || ((field.getModifiers() & BAD_MODIFIERS) != 0))
         continue;

       field.setAccessible(true);

       Object value;
       try
       {
         value = field.get(exampleObj);
       } catch (IllegalArgumentException e)
       {
         throw new RuntimeException(e);
       } catch (IllegalAccessException e)
       {
         throw new RuntimeException(e);
       }
       if (value != null)
       {
         q.filter(field.getName(), value);
       }
     }

     return q;
   }
   
   public Iterable<T> getByField(String field, Object value) {
		Iterable<T> objects = ofy().query(clazz).filter(field, value);
		return objects;
   }
}