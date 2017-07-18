package com.compomics.util.db;

import com.compomics.util.IdObject;
import com.compomics.util.waiting.WaitingHandler;
import java.io.*;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.Semaphore;

import javax.jdo.Extent;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import org.zoodb.jdo.ZooJdoHelper;
import org.zoodb.tools.ZooHelper;

/**
 * A database which can easily be used to store objects.
 *
 * @author Marc Vaudel
 */
public class ObjectsDB {

    /**
     * The version UID for serialization/deserialization compatibility.
     */
    static final long serialVersionUID = -8595805180622832745L;
    /**
     * The name of the database.
     */
    private String dbName;
    /**
     * The path to the database.
     */
    private String path;
    /**
     * The cache to be used for the objects.
     */
    private ObjectsCache objectsCache;
    /**
     * A boolean indicating whether the database is being queried.
     */
    private boolean loading = false;
    /**
     * Mutex for the interaction with the database.
     */
    private final Semaphore dbMutex = new Semaphore(1);
    /**
     * Debug, if true, all interaction with the database will be logged in the
     * System.out stream.
     */
    private static boolean debugInteractions = true;    
    /**
     * OrientDB database connection
     */
    PersistenceManager pm = null;
    /**
     * HashMap to map hash IDs of entries into DB ids
     */
    private final HashMap<Long, Long> idMap = new HashMap<Long, Long>();
    /**
     * path of the database folder
     */
    private String dbFolder = null;
    
    
    
    /**
     * Constructor.
     *
     * @param folder absolute path of the folder where to establish the database
     * @param dbName name of the database
     *
     * @throws SQLException exception thrown whenever a problem occurred when
     * establishing the connection to the database
     * @throws java.io.IOException exception thrown whenever an error occurred
     * while reading or writing a file
     * @throws java.lang.ClassNotFoundException exception thrown whenever an
     * error occurred while deserializing a file
     * @throws java.lang.InterruptedException exception thrown whenever a
     * threading error occurred while establishing the connection
     */
    public ObjectsDB(String folder, String dbName) throws SQLException, IOException, ClassNotFoundException, InterruptedException {
        this(folder, dbName, true);
    }
    
    /**
     * Constructor.
     *
     * @param folder absolute path of the folder where to establish the database
     * @param dbName name of the database
     * @param overwrite overwriting old database
     *
     * @throws SQLException exception thrown whenever a problem occurred when
     * establishing the connection to the database
     * @throws java.io.IOException exception thrown whenever an error occurred
     * while reading or writing a file
     * @throws java.lang.ClassNotFoundException exception thrown whenever an
     * error occurred while deserializing a file
     * @throws java.lang.InterruptedException exception thrown whenever a
     * threading error occurred while establishing the connection
     */
    public ObjectsDB(String folder, String dbName, boolean overwrite) throws SQLException, IOException, ClassNotFoundException, InterruptedException {
        File f = new File("/" + folder + "/" + dbName);
        dbFolder = f.getAbsolutePath();
        
        if (!f.exists()){
            if (!f.mkdirs()){
                throw new IOException("cannot create database folder");
            }
        }
        else if (overwrite) {
            ZooHelper.removeDb(dbName);
            f.mkdirs();
        }
        
        establishConnection();
        objectsCache = new ObjectsCache(this);
        
    }
    
    public HashMap<Long, Long> getIdMap(){
        return idMap;
    }
    
    
    public Semaphore getDbMutex(){
        return dbMutex;
    }
    
    
    public PersistenceManager getDB(){
        return pm;
    }
    
    
    public long createLongKey(String key){
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(StandardCharsets.UTF_8.encode(key));
            String md5Key = String.format("%032x", new BigInteger(1, md5.digest()));
            long longKey = 0;
            for (int i = 0; i < 32; ++i){
                longKey |= ((long)(md5Key.charAt(i) - '0')) << ((i * 11) % 63);
            }
            return longKey;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }
    

    /**
     * Returns the database name.
     *
     * @return the database name
     */
    public String getName() {
        return dbName;
    }

    /**
     * Returns the cache used by this database.
     *
     * @return the cache used by this database
     */
    public ObjectsCache getObjectsCache() {
        return objectsCache;
    }

    /**
     * Sets the object cache to be used by this database.
     *
     * @param objectCache the object cache to be used by this database
     */
    public void setObjectCache(ObjectsCache objectCache) {
        this.objectsCache = objectCache;
    }
    
    

    /**
     * Stores an object in the desired table. When multiple objects are to be
     * inserted, use insertObjects instead.
     *
     * @param objectKey the key of the object
     * @param object the object to store
     *
     * @throws SQLException exception thrown whenever an error occurred while
     * storing the object
     * @throws IOException exception thrown whenever an error occurred while
     * writing in the database
     * @throws InterruptedException exception thrown whenever a threading error
     * occurred while interacting with the database
     */
    public void insertObject(String objectKey, Object object) throws SQLException, IOException, InterruptedException {

        if (debugInteractions) {
            System.out.println(System.currentTimeMillis() + " Inserting single object " + object.getClass().getSimpleName() + ", key: " + objectKey);
        }
        
        long longKey = createLongKey(objectKey);
        ((IdObject)object).setId(longKey);
        objectsCache.addObject(longKey, object, true);
    }
    
    /**
     * Returns an iterator of all objects of a given class
     * @param className the class name
     * @return the iterator
     */
    public Iterator<?> getObjectsIterator(String className){
        return pm.getExtent(className.getClass()).iterator();
    }
    

    /**
     * Inserts a set of objects in the given table.
     *
     * @param objects map of the objects (object key &gt; object)
     * @param waitingHandler a waiting handler displaying the progress (can be
     * null). The progress will be displayed on the secondary progress bar.
     * @param displayProgress boolean indicating whether the progress of this
     * method should be displayed on the waiting handler
     *
     * @throws SQLException exception thrown whenever an error occurs while
     * interacting with the database
     * @throws IOException exception thrown whenever an error occurs while
     * reading or writing a file
     * @throws InterruptedException exception thrown whenever a threading error
     * occurred
     */
    public void insertObjects(HashMap<String, Object> objects, WaitingHandler waitingHandler, boolean displayProgress) throws SQLException, IOException, InterruptedException {
        
        
        for (String objectKey : objects.keySet()) {
            Object object = objects.get(objectKey);
            if (debugInteractions) {
                System.out.println(System.currentTimeMillis() + " Inserting single object, table: " + object.getClass().getName() + ", key: " + objectKey);
            }
            
            long longKey = createLongKey(objectKey);
            ((IdObject)object).setId(longKey);
            
            objectsCache.addObject(longKey, object, true);
            
            if (waitingHandler == null || !waitingHandler.isRunCanceled()) {
                pm.makePersistent(object);
                pm.currentTransaction().commit();
                pm.currentTransaction().begin();
            }
        }
    }
    
    

    /**
     * Loads some objects from a table in the cache.
     *
     * @param keys the keys of the objects to load
     * @param waitingHandler the waiting handler allowing displaying progress
     * and canceling the process
     * @param displayProgress boolean indicating whether the progress of this
     * method should be displayed on the waiting handler
     * @return returns the list of hashed keys
     *
     * @throws SQLException exception thrown whenever an error occurs while
     * interacting with the database
     * @throws IOException exception thrown whenever an error occurs while
     * reading or writing a file
     * @throws ClassNotFoundException exception thrown whenever an error
     * occurred while deserializing a file from the database
     * @throws InterruptedException exception thrown if a threading error occurs
     * while interacting with the database
     */
    public ArrayList<Long> loadObjects(ArrayList<String> keys, WaitingHandler waitingHandler, boolean displayProgress) throws SQLException, IOException, ClassNotFoundException, InterruptedException {
        if (debugInteractions) {
            System.out.println(System.currentTimeMillis() + " loading " + keys.size() + " objects");
        }
        
        dbMutex.acquire();
        HashMap<Long, Object> allObjects = new HashMap<Long, Object>();
        ArrayList<Long> hashedKeys = new ArrayList<Long>();
        for (String objectKey : keys){
            if (waitingHandler.isRunCanceled()) break;
            long longKey = createLongKey(objectKey);
            hashedKeys.add(longKey);
            Long zooid = idMap.get(longKey);
            if (zooid != null){
                Object obj = pm.getObjectById(zooid);
                if (!idMap.containsKey(longKey)){
                    idMap.put(longKey, (Long)pm.getObjectId(obj));
                }
                allObjects.put(longKey, obj);
            }
            
        }
        dbMutex.release();
        if (waitingHandler != null && !waitingHandler.isRunCanceled()){
            objectsCache.addObjects(allObjects, false);
        }
        
        return hashedKeys;
    }
    
    

    /**
     * Loads all objects from a given class.
     *
     * @param className the class name of the objects to be retrieved
     * @param waitingHandler the waiting handler allowing displaying progress
     * and canceling the process
     * @param displayProgress boolean indicating whether the progress of this
     * method should be displayed on the waiting handler
     * @return returns the list of hashed keys
     *
     * @throws SQLException exception thrown whenever an error occurs while
     * interacting with the database
     * @throws IOException exception thrown whenever an error occurs while
     * reading or writing a file
     * @throws ClassNotFoundException exception thrown whenever an error
     * occurred while deserializing a file from the database
     * @throws InterruptedException exception thrown if a threading error occurs
     * while interacting with the database
     */
    public ArrayList<Long> loadObjects(String className, WaitingHandler waitingHandler, boolean displayProgress) throws SQLException, IOException, ClassNotFoundException, InterruptedException {
        if (debugInteractions) {
            System.out.println(System.currentTimeMillis() + " retrieving all " + className + " objects");
        }
        
        
        dbMutex.acquire();
        HashMap<Long, Object> allObjects = new HashMap<Long, Object>();
        ArrayList<Long> hashedKeys = new ArrayList<Long>();
        for (Object obj : pm.getExtent(className.getClass())){
            if (waitingHandler.isRunCanceled()) break;
            long longKey = ((IdObject)obj).getId();
            hashedKeys.add(longKey);
            if (!idMap.containsKey(longKey)){
                idMap.put(longKey, (Long)pm.getObjectId(obj));
            }
            allObjects.put(longKey, obj);
            
        }
        dbMutex.release();
        if (waitingHandler != null && !waitingHandler.isRunCanceled()){
            objectsCache.addObjects(allObjects, false);
        }
        return hashedKeys;
    }
    
    
    /**
     * Loads some objects from a table in the cache.
     *
     * @param iterator the iterator
     * @param num number of objects that have to be retrieved in a batch
     * @param waitingHandler the waiting handler allowing displaying progress
     * and canceling the process
     * @param displayProgress boolean indicating whether the progress of this
     * method should be displayed on the waiting handler
     * @return returns the list of hashed keys
     *
     * @throws SQLException exception thrown whenever an error occurs while
     * interacting with the database
     * @throws IOException exception thrown whenever an error occurs while
     * reading or writing a file
     * @throws ClassNotFoundException exception thrown whenever an error
     * occurred while deserializing a file from the database
     * @throws InterruptedException exception thrown if a threading error occurs
     * while interacting with the database
     */
    public ArrayList<Long> loadObjects(Iterator<?> iterator, int num, WaitingHandler waitingHandler, boolean displayProgress) throws SQLException, IOException, ClassNotFoundException, InterruptedException {
        if (debugInteractions) {
            System.out.println(System.currentTimeMillis() + " loading " + num + " objects");
        }
        
        dbMutex.acquire();
        HashMap<Long, Object> allObjects = new HashMap<Long, Object>();
        ArrayList<Long> hashedKeys = new ArrayList<Long>();
        while(num > 0 && iterator.hasNext()){
            if (waitingHandler.isRunCanceled()) break;
            Object obj = iterator.next();
            long longKey = ((IdObject)obj).getId();
            hashedKeys.add(longKey);
            Long zooid = idMap.get(longKey);
            if (zooid != null){
                if (!idMap.containsKey(longKey)){
                    idMap.put(longKey, (Long)pm.getObjectId(obj));
                }
            }
            allObjects.put(longKey, obj);
            num--;
        }
        dbMutex.release();
        if (waitingHandler != null && !waitingHandler.isRunCanceled()){
            objectsCache.addObjects(allObjects, false);
        }
        return hashedKeys;
    }
    
    
    /**
     * retrieves some objects from the database or cache.
     *
     * @param longKey the keys of the object to load
     * @return the retrived objcets
     *
     * @throws SQLException exception thrown whenever an error occurs while
     * interacting with the database
     * @throws IOException exception thrown whenever an error occurs while
     * reading or writing a file
     * @throws ClassNotFoundException exception thrown whenever an error
     * occurred while deserializing a file from the database
     * @throws InterruptedException exception thrown if a threading error occurs
     * while interacting with the database
     */
    public Object retrieveObject(long longKey) throws SQLException, IOException, ClassNotFoundException, InterruptedException {
        if (debugInteractions) {
            System.out.println(System.currentTimeMillis() + " | retrieving one objects with key: " + longKey);
        }
                
        Object obj = objectsCache.getObject(longKey);
        dbMutex.acquire();
        if (obj == null){
            Long zooid = idMap.get(longKey);
            if (zooid != null){
                obj = pm.getObjectById(zooid);
                if (!idMap.containsKey(longKey)){
                    idMap.put(longKey, (Long)pm.getObjectId(obj));
                }
            }
            objectsCache.addObject(longKey, obj, false);
        }
        dbMutex.release();
        return obj;
    }
    
    
    /**
     * retrieves some objects from the database or cache.
     *
     * @param key the keys of the object to load
     * @return the retrieved objcets
     *
     * @throws SQLException exception thrown whenever an error occurs while
     * interacting with the database
     * @throws IOException exception thrown whenever an error occurs while
     * reading or writing a file
     * @throws ClassNotFoundException exception thrown whenever an error
     * occurred while deserializing a file from the database
     * @throws InterruptedException exception thrown if a threading error occurs
     * while interacting with the database
     */
    public Object retrieveObject(String key) throws SQLException, IOException, ClassNotFoundException, InterruptedException {
        if (debugInteractions) {
            System.out.println(System.currentTimeMillis() + " retrieving one objects with key: " + key);
        }
        return retrieveObject(createLongKey(key));
    }
    
    

    /**
     * Returns the number of instances of a given class stored in the db
     *
     * @param className the class name of the objects to be load
     * @return the number of objects
     *
     * @throws SQLException exception thrown whenever an error occurs while
     * interacting with the database
     * @throws IOException exception thrown whenever an error occurs while
     * reading or writing a file
     * @throws ClassNotFoundException exception thrown whenever an error
     * occurred while deserializing a file from the database
     * @throws InterruptedException exception thrown if a threading error occurs
     * while interacting with the database
     */
    public int getNumber(String className) throws SQLException, IOException, ClassNotFoundException, InterruptedException {
        if (debugInteractions) {
            System.out.println(System.currentTimeMillis() + " query number of " + className + " objects");
        }
        
        int num = 0;
        dbMutex.acquire();
        Query q = pm.newQuery(className.getClass());
        num = ((Collection<?>) q.execute()).size();
        dbMutex.release();
        return num;
    }
    
    
    /**
     * Clears the cache and dumps everything into the database.
     * 
     *
     * @throws IOException if an IOException occurs while writing to the
     * database
     * @throws SQLException if an SQLException occurs while writing to the
     * database
     * @throws java.lang.InterruptedException if a threading error occurs
     * writing to the database
     */
    public void clearCache() throws IOException, SQLException, InterruptedException {
        objectsCache.clearCache();
    }
    
    
    /**
     * retrieves some objects from the database or cache.
     *
     * @param keys the keys of the objects to load
     * @param waitingHandler the waiting handler allowing displaying progress
     * and canceling the process
     * @param displayProgress boolean indicating whether the progress of this
     * method should be displayed on the waiting handler
     * @return a list of objcets
     *
     * @throws SQLException exception thrown whenever an error occurs while
     * interacting with the database
     * @throws IOException exception thrown whenever an error occurs while
     * reading or writing a file
     * @throws ClassNotFoundException exception thrown whenever an error
     * occurred while deserializing a file from the database
     * @throws InterruptedException exception thrown if a threading error occurs
     * while interacting with the database
     */
    public ArrayList<Object> retrieveObjects(ArrayList<String> keys, WaitingHandler waitingHandler, boolean displayProgress) throws SQLException, IOException, ClassNotFoundException, InterruptedException {
        if (debugInteractions) {
            System.out.println(System.currentTimeMillis() + " retrieving " + keys.size() + " objects");
        }
        
        dbMutex.acquire();
        ArrayList<Object> retrievingObjects = new ArrayList<Object>();
        HashMap<Long, Object> allObjects = new HashMap<Long, Object>();
        for (String objectKey : keys){
            if (waitingHandler.isRunCanceled()) break;
            long longKey = createLongKey(objectKey);
            Object obj = objectsCache.getObject(longKey);
            if (obj == null){
                
                Long zooid = idMap.get(longKey);
                if (zooid != null){
                    obj = pm.getObjectById(zooid);
                    if (!idMap.containsKey(longKey)){
                        idMap.put(longKey, (Long)pm.getObjectId(obj));
                    }
                    allObjects.put(longKey, obj);
                }
            
            }
            retrievingObjects.add(obj);
        }
        dbMutex.release();
        if (waitingHandler != null && !waitingHandler.isRunCanceled()){
            objectsCache.addObjects(allObjects, false);
        }
        return retrievingObjects;
    }
    
    

    /**
     * Retrieves all objects from a given class.
     *
     * @param className the class name of the objects to be retrieved
     * @param waitingHandler the waiting handler allowing displaying progress
     * and canceling the process
     * @param displayProgress boolean indicating whether the progress of this
     * method should be displayed on the waiting handler
     * @return the list of objects
     *
     * @throws SQLException exception thrown whenever an error occurs while
     * interacting with the database
     * @throws IOException exception thrown whenever an error occurs while
     * reading or writing a file
     * @throws ClassNotFoundException exception thrown whenever an error
     * occurred while deserializing a file from the database
     * @throws InterruptedException exception thrown if a threading error occurs
     * while interacting with the database
     */
    public ArrayList<Object> retrieveObjects(String className, WaitingHandler waitingHandler, boolean displayProgress) throws SQLException, IOException, ClassNotFoundException, InterruptedException {
        if (debugInteractions) {
            System.out.println(System.currentTimeMillis() + " retrieving all " + className + " objects");
        }
        
        
        dbMutex.acquire();
        HashMap<Long, Object> allObjects = new HashMap<Long, Object>();
        ArrayList<Object> retrievingObjects = new ArrayList<Object>();
        for (Object obj : pm.getExtent(className.getClass())){
            if (waitingHandler.isRunCanceled()) break;
            long longKey = ((IdObject)obj).getId();
            if (!idMap.containsKey(longKey)){
                idMap.put(longKey, (Long)pm.getObjectId(obj));
            }
            allObjects.put(longKey, obj);
            retrievingObjects.add(obj);
            
        }
        dbMutex.release();
        if (waitingHandler != null && !waitingHandler.isRunCanceled()){
            objectsCache.addObjects(allObjects, false);
        }
        return retrievingObjects;
    }
    
    
    

    /**
     * Removing an object from.
     *
     * @param keys the object key
     * @param waitingHandler the waiting handler allowing displaying progress
     * and canceling the process
     * @param displayProgress boolean indicating whether the progress of this
     * method should be displayed on the waiting handler
     *
     * @throws SQLException exception thrown whenever an error occurs while
     * interacting with the database
     * @throws IOException exception thrown whenever an error occurs while
     * reading or writing a file
     * @throws ClassNotFoundException exception thrown whenever an error
     * occurred while deserializing a file from the database
     * @throws InterruptedException exception thrown if a threading error occurs
     * while interacting with the database
     */
    public void removeObjects(ArrayList<String> keys, WaitingHandler waitingHandler, boolean displayProgress) throws SQLException, IOException, ClassNotFoundException, InterruptedException {
        if (debugInteractions) {
            System.out.println(System.currentTimeMillis() + " removing " + keys.size() + " objects");
        }
        
        dbMutex.acquire();
        
        for (String key : keys){
            if (waitingHandler.isRunCanceled()) break;
            long longKey = createLongKey(key);
            objectsCache.removeObject(longKey);
            dbMutex.acquire();
            Long zooid = idMap.get(longKey);
            if (zooid != null){
                pm.deletePersistent(pm.getObjectById((zooid)));
                idMap.remove(longKey);
            }
        }
        dbMutex.release();
    }
    
    
    

    /**
     * Removing an object from.
     *
     * @param key the object key
     *
     * @throws SQLException exception thrown whenever an error occurs while
     * interacting with the database
     * @throws IOException exception thrown whenever an error occurs while
     * reading or writing a file
     * @throws ClassNotFoundException exception thrown whenever an error
     * occurred while deserializing a file from the database
     * @throws InterruptedException exception thrown if a threading error occurs
     * while interacting with the database
     */
    public void removeObject(String key) throws SQLException, IOException, ClassNotFoundException, InterruptedException {
        if (debugInteractions) {
            System.out.println(System.currentTimeMillis() + " removing object: " + key);
        }
        
        
        long longKey = createLongKey(key);
        objectsCache.removeObject(longKey);
        dbMutex.acquire();
        Long zooid = idMap.get(longKey);
        if (zooid != null){
            pm.deletePersistent(pm.getObjectById((zooid)));
            idMap.remove(longKey);
        }
        dbMutex.release();
    }
    
    
    

    /**
     * Indicates whether an object is loaded.
     *
     * @param objectKey the object key
     *
     * @return a boolean indicating whether an object is loaded
     *
     * @throws SQLException exception thrown whenever an exception occurred
     * while interrogating the database
     * @throws InterruptedException exception thrown if a threading error occurs
     */
    public boolean inCache(String objectKey) throws SQLException, InterruptedException {
        return objectsCache.inCache(createLongKey(objectKey));
    }
    
    

    /**
     * Indicates whether an object is loaded.
     *
     * @param objectKey the object key
     *
     * @return a boolean indicating whether an object is loaded
     *
     * @throws SQLException exception thrown whenever an exception occurred
     * while interrogating the database
     * @throws InterruptedException exception thrown if a threading error occurs
     */
    public boolean inDB(String objectKey) throws SQLException, InterruptedException {

        long longKey = createLongKey(objectKey);

        if (objectsCache.inCache(longKey)) {
            return true;
        }

        return savedInDB(objectKey);
    }

    /**
     * Indicates whether an object is saved.
     *
     * @param objectKey the object key
     *
     * @return a boolean indicating whether an object is saved
     *
     * @throws SQLException exception thrown whenever an exception occurred
     * while interrogating the database
     * @throws InterruptedException exception thrown if a threading error occurs
     */
    private boolean savedInDB(String objectKey) throws SQLException, InterruptedException {

        if (debugInteractions) {
            System.out.println(System.currentTimeMillis() + " Checking db content,  key: " + objectKey);
        }
        
        long longKey = createLongKey(objectKey);
        
        dbMutex.acquire();
        Query q = pm.newQuery(IdObject.class, "id == " + longKey);
        if( ((Collection<?>) q.execute()).size() > 0){
            dbMutex.release();
            return true;
        }
        dbMutex.release();
        return false;
    }   



    /**
     * Indicates whether the connection to the DB is active.
     *
     * @return true if the connection to the DB is active
     */
    public boolean isConnectionActive() {
        return true;
        // TODO: check for orientDB
    }

    /**
     * Closes the db connection.
     *
     * @throws SQLException exception thrown whenever an error occurred while
     * closing the database connection
     * @throws InterruptedException exception thrown if a threading error occurs
     * @throws java.io.IOException exception thrown whenever an error occurred while
     * writing the object
     */
    public void close() throws SQLException, InterruptedException, IOException {
        
        
        dbMutex.acquire();
        pm.currentTransaction().commit();
        if (pm.currentTransaction().isActive()) {
            pm.currentTransaction().rollback();
        }
        pm.close();
        pm.getPersistenceManagerFactory().close();
        dbMutex.release();

    }

    /**
     * Establishes connection to the database.
     *
     * @throws SQLException exception thrown whenever an error occurred while
     * establishing the connection to the database
     * @throws java.io.IOException exception thrown whenever an error occurred
     * while reading or writing a file
     * @throws java.lang.ClassNotFoundException exception thrown whenever an
     * error occurred while deserializing a file
     * @throws java.lang.InterruptedException exception thrown whenever a
     * threading error occurred while establishing the connection
     */
    public void establishConnection() throws SQLException, IOException, ClassNotFoundException, InterruptedException {

        dbMutex.acquire();
        
        PersistenceManager pm = ZooJdoHelper.openOrCreateDB(dbFolder);
        pm.currentTransaction().begin();
        
        dbMutex.release();
        
        // TODO: load project parameters
    }
    
    

    /**
     * Returns the path to the database.
     *
     * @return the path to the database
     */
    public String getPath() {
        return path;
    }

    /**
     * Turn the debugging of interactions on or off.
     *
     * @param debug if true, the debugging is turned on
     */
    public static void setDebugInteractions(boolean debug) {
        debugInteractions = debug;
    }
}
