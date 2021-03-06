package org.jboss.as.clustering.infinispan.subsystem;

import static org.jboss.as.clustering.infinispan.subsystem.ModelKeys.DEFAULT_CACHE;
import static org.jboss.as.clustering.infinispan.subsystem.ModelKeys.JNDI_NAME;
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.*;

import static org.junit.Assert.*;

import java.io.IOException;
import java.lang.reflect.Field;

import org.jboss.as.controller.ControlledProcessState;
import org.jboss.as.controller.PathAddress;
import org.jboss.as.controller.operations.common.Util;
import org.jboss.as.model.test.ModelTestModelControllerService;
import org.jboss.as.subsystem.test.AbstractSubsystemTest;
import org.jboss.as.subsystem.test.AdditionalInitialization;
import org.jboss.as.subsystem.test.KernelServices;
import org.jboss.as.subsystem.test.KernelServicesBuilder;
import org.jboss.dmr.ModelNode;

/**
* Base test case for testing management operations.
*
* @author Richard Achmatowicz (c) 2011 Red Hat Inc.
*/

public class OperationTestCaseBase extends AbstractSubsystemTest {

    static final String SUBSYSTEM_XML_FILE = "subsystem-infinispan_9_0.xml" ;
    static final String JDBC_STORE_NAME = "jdbc-store" ;

    public OperationTestCaseBase() {
        super(InfinispanExtension.SUBSYSTEM_NAME, new InfinispanExtension());
    }

    KernelServicesBuilder createKernelServicesBuilder() {
       return this.createKernelServicesBuilder(this.createAdditionalInitialization());
    }

    AdditionalInitialization createAdditionalInitialization() {
       return new InfinispanSubsystemDependenciesInitialization();
    }

    // cache container access
    protected static ModelNode getCacheContainerAddOperation(String containerName) {
        // create the address of the cache
        PathAddress containerAddr = getCacheContainerAddress(containerName);
        ModelNode addOp = Util.createAddOperation(containerAddr);
        // required attributes
        addOp.get(DEFAULT_CACHE).set("default");
        return addOp ;
    }

    protected static ModelNode getCacheContainerReadOperation(String containerName, String name) {
        // create the address of the subsystem
        PathAddress transportAddress = getCacheContainerAddress(containerName);
        ModelNode readOp = new ModelNode() ;
        readOp.get(OP).set(READ_ATTRIBUTE_OPERATION);
        readOp.get(OP_ADDR).set(transportAddress.toModelNode());
        // required attributes
        readOp.get(NAME).set(name);
        return readOp ;
    }

    protected static ModelNode getCacheContainerAddAliasOperation(String containerName, String name, String value) {
        // create the address of the subsystem
        PathAddress cacheContainerAddress = getCacheContainerAddress(containerName);
        ModelNode addAliasOp = new ModelNode() ;
        addAliasOp.get(OP).set("add-alias");
        addAliasOp.get(OP_ADDR).set(cacheContainerAddress.toModelNode());
        // required attributes
        addAliasOp.get(NAME).set(name);
        return addAliasOp ;
    }

    protected static ModelNode getCacheContainerRemoveAliasOperation(String containerName, String name) {
        // create the address of the subsystem
        PathAddress cacheContainerAddress = getCacheContainerAddress(containerName);
        ModelNode removeAliasOp = new ModelNode() ;
        removeAliasOp.get(OP).set("remove-alias");
        removeAliasOp.get(OP_ADDR).set(cacheContainerAddress.toModelNode());
        // required attributes
        removeAliasOp.get(NAME).set(name);
        return removeAliasOp ;
    }

    protected static ModelNode getCacheContainerWriteOperation(String containerName, String name, String value) {
        // create the address of the subsystem
        PathAddress cacheAddress = getCacheContainerAddress(containerName);
        return Util.getWriteAttributeOperation(cacheAddress, name, new ModelNode().set(value));
    }

    protected static ModelNode getCacheContainerRemoveOperation(String containerName) {
        // create the address of the cache
        PathAddress containerAddr = getCacheContainerAddress(containerName);
        return Util.createRemoveOperation(containerAddr);
    }

    // cache access
    protected static ModelNode getCacheAddOperation(String containerName, String cacheType, String cacheName) {
        // create the address of the cache
        PathAddress cacheAddr = getCacheAddress(containerName, cacheType, cacheName);
        ModelNode addOp = Util.createAddOperation(cacheAddr);
        // required attributes
        addOp.get(JNDI_NAME).set("java:/fred/was/here");
        return addOp ;
    }

    protected static ModelNode getCacheConfigurationReadOperation(String containerName, String cacheType, String cacheName, String name) {
        // create the address of the subsystem
        PathAddress cacheAddress = getCacheConfigurationAddress(containerName, cacheType, cacheName);
        ModelNode readOp = new ModelNode() ;
        readOp.get(OP).set(READ_ATTRIBUTE_OPERATION);
        readOp.get(OP_ADDR).set(cacheAddress.toModelNode());
        // required attributes
        readOp.get(NAME).set(name);
        return readOp ;
    }

    protected static ModelNode getCacheReadOperation(String containerName, String cacheType, String cacheName, String name) {
        // create the address of the subsystem
        PathAddress cacheAddress = getCacheAddress(containerName, cacheType, cacheName);
        ModelNode readOp = new ModelNode() ;
        readOp.get(OP).set(READ_ATTRIBUTE_OPERATION);
        readOp.get(OP_ADDR).set(cacheAddress.toModelNode());
        // required attributes
        readOp.get(NAME).set(name);
        return readOp ;
    }

    protected static ModelNode getCacheConfigurationWriteOperation(String containerName, String cacheType, String cacheName, String name, String value) {
        PathAddress cacheAddress = getCacheConfigurationAddress(containerName, cacheType, cacheName);
        return Util.getWriteAttributeOperation(cacheAddress, name, new ModelNode().set(value));
    }

    protected static ModelNode getCacheWriteOperation(String containerName, String cacheType, String cacheName, String name, String value) {
        PathAddress cacheAddress = getCacheAddress(containerName, cacheType, cacheName);
        return Util.getWriteAttributeOperation(cacheAddress, name, new ModelNode().set(value));
    }

    protected static ModelNode getCacheRemoveOperation(String containerName, String cacheType, String cacheName) {
        PathAddress cacheAddr = getCacheAddress(containerName, cacheType, cacheName);
        return Util.createRemoveOperation(cacheAddr) ;
    }

    // cache store access
    protected static ModelNode getCacheStoreReadOperation(String containerName, String cacheType, String cacheName, String name) {
        // create the address of the subsystem
        PathAddress cacheStoreAddress = getCacheStoreAddress(containerName, cacheType, cacheName);
        ModelNode readOp = new ModelNode() ;
        readOp.get(OP).set(READ_ATTRIBUTE_OPERATION);
        readOp.get(OP_ADDR).set(cacheStoreAddress.toModelNode());
        // required attributes
        readOp.get(NAME).set(name);
        return readOp ;
    }

    protected static ModelNode getCacheStoreWriteOperation(String containerName, String cacheName, String cacheType, String name, String value) {
        PathAddress cacheStoreAddress = getCacheStoreAddress(containerName,  cacheType, cacheName);
        return Util.getWriteAttributeOperation(cacheStoreAddress, name, new ModelNode().set(value));
    }

    protected static ModelNode getMixedKeyedJDBCCacheStoreReadOperation(String containerName, String cacheType, String cacheName, String name) {
        // create the address of the subsystem
        PathAddress cacheAddress = getMixedKeyedJDBCCacheStoreAddress(containerName, cacheType, cacheName);
        ModelNode readOp = new ModelNode() ;
        readOp.get(OP).set(READ_ATTRIBUTE_OPERATION);
        readOp.get(OP_ADDR).set(cacheAddress.toModelNode());
        // required attributes
        readOp.get(NAME).set(name);
        return readOp ;
    }

    protected static ModelNode getMixedKeyedJDBCCacheStoreWriteOperation(String containerName, String cacheType, String cacheName, String name, String value) {
        PathAddress cacheStoreAddress = getMixedKeyedJDBCCacheStoreAddress(containerName, cacheType, cacheName);
        return Util.getWriteAttributeOperation(cacheStoreAddress, name, new ModelNode().set(value));
    }

    protected static ModelNode getMixedKeyedJDBCCacheStoreWriteOperation(String containerName, String cacheType, String cacheName, String name, ModelNode value) {
        PathAddress cacheStoreAddress = getMixedKeyedJDBCCacheStoreAddress(containerName, cacheType, cacheName);
        return Util.getWriteAttributeOperation(cacheStoreAddress, name, value);
    }

    //cache store property access
    protected static ModelNode getCacheStorePropertyAddOperation(String containerName, String cacheName, String cacheType, String propertyName, String value) {
        PathAddress cacheStorePropertyAddress = getCacheStorePropertyAddress(containerName,  cacheType, cacheName, propertyName);
        ModelNode addOp = Util.createAddOperation(cacheStorePropertyAddress);
        // required attributes
        addOp.get(VALUE).set(value);
        return addOp ;
    }

    protected static ModelNode getCacheStorePropertyWriteOperation(String containerName, String cacheName, String cacheType, String propertyName, String value) {
        PathAddress cacheStorePropertyAddress = getCacheStorePropertyAddress(containerName, cacheType, cacheName, propertyName);
        return Util.getWriteAttributeOperation(cacheStorePropertyAddress, "value", new ModelNode().set(value));
    }

    // address generation
    protected static PathAddress getCacheStorePropertyAddress(String containerName, String cacheType, String cacheName, String propertyName) {
        return getCacheStoreAddress(containerName, cacheType, cacheName).append(ModelKeys.PROPERTY, propertyName);
    }

    protected static PathAddress getMixedKeyedJDBCCacheStoreAddress(String containerName, String cacheType, String cacheName) {
        return getCacheConfigurationAddress(containerName, cacheType, cacheName).append(ModelKeys.MIXED_KEYED_JDBC_STORE, JDBC_STORE_NAME);
    }

    protected static PathAddress getBinaryKeyedJDBCCacheStoreAddress(String containerName, String cacheType, String cacheName) {
        return getCacheConfigurationAddress(containerName, cacheType, cacheName).append(ModelKeys.BINARY_KEYED_JDBC_STORE, ModelKeys.BINARY_KEYED_JDBC_STORE_NAME);
    }

    protected static PathAddress getStringKeyedJDBCCacheStoreAddress(String containerName, String cacheType, String cacheName) {
        return getCacheConfigurationAddress(containerName, cacheType, cacheName).append(ModelKeys.STRING_KEYED_JDBC_STORE, ModelKeys.STRING_KEYED_JDBC_STORE_NAME);
    }

    protected static PathAddress getRemoteCacheStoreAddress(String containerName, String cacheType, String cacheName) {
        return getCacheConfigurationAddress(containerName, cacheType, cacheName).append(ModelKeys.REMOTE_STORE, ModelKeys.REMOTE_STORE_NAME);
    }

    protected static PathAddress getFileCacheStoreAddress(String containerName, String cacheType, String cacheName) {
        return getCacheAddress(containerName, cacheType, cacheName).append(ModelKeys.FILE_STORE, ModelKeys.FILE_STORE_NAME);
    }

    protected static PathAddress getCacheStoreAddress(String containerName, String cacheType, String cacheName) {
        return getCacheAddress(containerName, cacheType, cacheName).append(ModelKeys.STORE, ModelKeys.STORE_NAME);
    }

    protected static PathAddress getCacheContainerAddress(String containerName) {
        return PathAddress.pathAddress(InfinispanExtension.SUBSYSTEM_PATH).append(ModelKeys.CACHE_CONTAINER, containerName);
    }

    protected static PathAddress getCacheAddress(String containerName, String cacheType, String cacheName) {
        return getCacheContainerAddress(containerName).append(cacheType, cacheName);
    }

    protected static PathAddress getCacheConfigurationAddress(String containerName, String cacheType, String cacheName) {
        return getCacheContainerAddress(containerName).append(ModelKeys.CONFIGURATIONS, ModelKeys.CONFIGURATIONS_NAME).append(cacheType, cacheName);
    }

    protected String getSubsystemXml() throws IOException {
        return readResource(SUBSYSTEM_XML_FILE) ;
    }

    protected void assertServerState(final KernelServices services, String expected) throws Exception {
        ModelTestModelControllerService controllerService = extractField(services, "controllerService");
        ControlledProcessState processState = extractField(controllerService, "processState");
        assertEquals(expected, processState.getState().toString());
    }

    public static <T> T extractField(Object target, String fieldName) {
        return (T) extractField(target.getClass(), target, fieldName);
    }

    public static Object extractField(Class type, Object target, String fieldName) {
        while (true) {
            Field field;
            try {
                field = type.getDeclaredField(fieldName);
                field.setAccessible(true);
                return field.get(target);
            } catch (Exception e) {
                if (type.equals(Object.class)) {
                    e.printStackTrace();
                    return null;
                } else {
                    // try with superclass!!
                    type = type.getSuperclass();
                }
            }
        }
    }

}
