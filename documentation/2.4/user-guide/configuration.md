---
---
# Cache Configuration


 


Caches can be configured in Ehcache either declaratively, in XML, or by creating them programmatically and specifying their parameters in the constructor.

While both approaches are fully supported it is generally a good idea to separate the cache configuration from runtime use. There are also these benefits:

* It is easy if you have all of your configuration in one place.
  Caches consume memory, and disk space. They need to be carefully tuned.
  You can see the total effect in a configuration file. You could do this
  code, but it would not as visible.
* Cache configuration can be changed at deployment time.
* Configuration errors can be checked for at start-up, rather than causing a runtime error.

This chapter covers XML declarative configuration.
Ehcache is redistributed by lots of projects. They may or may not provide a sample Ehcache XML configuration file.
If one is not provided, download Ehcache from [http://ehcache.org](http://ehcache.org). ehcache.xml and ehcache.xsd are provided in
the distribution.

## Dynamically Changing Cache Configuration

After a Cache has been started, its configuration is not generally changeable. However, since Ehcache 2.0,  certain cache configuration parameters can be modified dynamically at runtime. In the current version of Ehcache, this includes the following:

* timeToLive

    The maximum number of seconds an element can exist in the cache regardless of use. The element expires at this limit and will no longer be returned from the cache. The default value is 0, which means no TTL eviction takes place (infinite lifetime).
    
* timeToIdle

    The maximum number of seconds an element can exist in the cache without being accessed. The element expires at this limit and will no longer be returned from the cache. The default value is 0, which means no TTI eviction takes place (infinite lifetime).
    
* maxEntriesLocalHeap
* maxEntriesLocalDisk
* memory-store eviction policy
* CacheEventListeners can be added and removed dynamically

Note that the `eternal` attribute, when set to "true", overrides `timeToLive` and `timeToIdle` so that no expiration can take place.
This example shows how to dynamically modify the cache configuration of an already running cache:

    Cache cache = manager.getCache("sampleCache");
    CacheConfiguration config = cache.getCacheConfiguration();
    config.setTimeToIdleSeconds(60);
    config.setTimeToLiveSeconds(120);
    config.setmaxEntriesLocalHeap(10000);
    config.setmaxEntriesLocalDisk(1000000);
    
Dynamic cache configurations can also be frozen to prevent future changes:

    Cache cache = manager.getCache("sampleCache");
    cache.disableDynamicFeatures();

## Memory Based Cache Sizing (Ehcache 2.5 and higher)

Historically Ehcache has only permitted sizing of caches by maxEntriesLocalHeap for the the OnHeap Store and maxEntriesLocalDisk
for the DiskStore. The OffHeap Store introduced sizing in terms of memory use.
From Ehcache 2.5, we are extending sizing based on bytes consumed to all stores.

The new cache attributes are:

*   maxBytesLocalHeap
*   maxBytesLocalOffHeap (formerly maxMemoryOffHeap)
*   maxBytesLocalDisk

Size may be expressed in bytes using the convention for specifying -Xmx (e.g. 200k, 30m, 5g etc.)

For added simplicity you can also specify these attributes at the ehcache level, which then applies them to the whole CacheManager,
leaving each cache to share in one large pool of memory.

    <ehcache xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    	xsi:noNamespaceSchemaLocation="ehcache.xsd"
    	maxBytesLocalHeap="1M" maxBytesLocalOffHeap="16M">
      <defaultCache eternal="true" overflowToOffHeap="true"/>
    </ehcache>
    

If you specify a CacheManager-wide size, you can also use percentages at the cache level. For example, you can specify `maxBytesLocalHeap="20%"`. A warning is issued if more than 100% is allocated across all caches.
For completeness we also add cache pinning and rules for cache-level configuration to override CacheManager-level configuration.

### Example Configuration

An example is shown below. It allocates 1GB on heap and 4GB off heap at the CacheManager level.
It also demonstrates some finer points which we will cover in the following sections.

<pre>
&lt;ehcache maxBytesLocalHeap="1g" maxBytesLocalOffHeap="4g" maxBytesLocalDisk="100g" &gt;
&lt;cache name="explicitlyAllocatedCache1"
     	maxBytesLocalHeap="50m"
     	maxBytesLocalOffHeap="200m"
     	timeToLiveSeconds="100"&gt;
&lt;/cache&gt;
&lt;cache name="explicitlyAllocatedCache2"
     	maxLocalHeap="10%"
     	maxBytesLocalOffHeap="200m"
     	timeToLiveSeconds="100"&gt;
&lt;/cache&gt;
&lt;!-- overflows automatically to off heap and disk because no specific override and resources are set at the CacheManager level --&gt;
&lt;cache name="automaticallyAllocatedCache1"
     	timeToLiveSeconds="100"&gt;
&lt;/cache&gt;
&lt;!-- Will share in OnHeap but not the other resource pools --&gt;
&lt;cache name="automaticallyAllocatedCache2"
   timeToLiveSeconds="100"
overflowToOffHeap="false"
overflowToDisk="false"&gt;
&lt;/cache&gt;
&lt;cache name="pinnedCache"
     	timeToLiveSeconds="100"
     	&lt;pinning store="inMemory"&gt;
&lt;/cache&gt;
&lt;/ehcache&gt;
</pre>

### CacheManager versus Cache level configuration

Caches without specific configuration participate in the general storage pools. And caches with specific configuration take
either a fixed amount (e.g. 200m) or a percentage (e.g. 5%).

If CacheManager level offheap and disk resources are allocated, then caches that do not specify overflow behaviour will overflow.

The CacheManager level storage pool attributes are:

*   maxBytesLocalHeap="size"
*   maxBytesLocalOffHeap="size"
*   maxBytesLocalDisk="size" 

where size is the Java -Xmx syntax. e.g. 4g

If a store is configured using a CacheManager level pool, the maxElements form of configuration cannot be used.

#### Cache-Level Overrides

There will be times when the developer knows more about the tuning of each cache than and can outperform CacheManager level
  tuning. In this case it is recommended to provide cache specific configuration.

Cache specific configuration always overrides CacheManager allocations.

The Cache level storage pool attributes are:

*   maxBytesLocalHeap="size | %"
*   maxBytesLocalOffHeap="size | %"
*   maxBytesLocalDisk="size | %"

where size is the Java -Xmx syntax. e.g. 4g and % is simply a positive number between 0 and 100. e.g. 5%

#### Overallocation Rules

To prevent overallocation of CacheManager level pools by cache level overrides we perform a number of checks on startup:

*   We convert percentages to fixed amounts
*   We then add the those to any other fixed allocations
*   If the sum exceeds the CacheManager allocation, we throw an `InvalidConfigurationException`.
*   If the sum equals the CacheManager allocation, we issue a warning, as there will not be memory left for caches without overrides

Overallocations can only be detected at configuration time. For this reason we do not permit the use of element-count configuration
(e.g. maxEntriesLocalHeap) with CacheManager storage pools.

If CacheManager-level storage pools are not used, then caches can use either size or count-based configuration on a per-cache basis. Moreover, caches must explicity configure either maxEntriesLocalHeap or maxBytesLocalHeap.


#### Sizing of cached entries

Elements put in a memory limited cache will have their memory sizes measured. The entire Element instance added
 to the cache is measured, including key and value, as well as the memory footprint of adding that instance to
 internal data structures. Key and value are measured as object graphs - each reference is followed and the object
 reference also measured. This goes on recursively.

Shared references will be measured by each class that references it. This will result in an overstatement. Shared references
 should therefore be ignored.

#### Ignoring for size calculations

References can be ignored using the `@IgnoreSizeOf` annotation.
 The annotation may be declared at the class level or on field.
 with the fully qualified class name of classes to be ingored and/or fields to be ignored during the measurement
 when adding an new entry to the cache.

This example shows how to ignore the `Dog` class.

<pre>
@IgnoreSizeOf
public class Dog {
  private Gender gender;
  private String name;
"/>
</pre>

This shows how to ignore the `sharedInstance` field.

<pre>
public class MyCacheEntry {
  @IgnoreSizeOf
  private final SharedClass sharedInstance;
    ...
"/>
</pre>

Finally packages may be also ignored if you add the @IgnoreSizeOf annotation to appropriate package-info.java of the desired package.
Here is a sample package-info.java for and in the com.pany.ignore package:

<pre>
@IgnoreSizeOf
package com.pany.ignore;
import net.sf.ehcache.pool.sizeof.filter.IgnoreSizeOf;
</pre>

 Alternatively you may declare ignored classes and fields in a file and specify a `net.sf.ehcache.sizeof.filter` system property
 to point to file.

<pre>
# That field references a common graph between all cached entries
com.pany.domain.cache.MyCacheEntry.sharedInstance

# This will ignore all instances of that type
com.pany.domain.SharedState

# This ignores a package
com.pany.example
</pre>

Note that these measurements and configurations only apply to on heap storage. Once Elements are moved to off-heap, disk or Terracotta, they
are serialized as byte arrays. The serialized size is then used as the basis for measurement.

#### Eviction when using CacheManager level storage

When a CacheManager level storage pool is exhausted a cache is selected on which to perform eviction to recover pool space.  The eviction from
 the selected cache is performed using the cache's configured eviction algorithm (LRU, LFU, etc...).
 The cache from which eviction is performed is selected using the "minimal eviction cost" algorithm described below.
 Eviction cost is defined as the increase in bytes requested from the underlying SOR (the database for example) per unit time that will be caused
 by evicting the requested number of bytes from the cache.

<pre>
 eviction-cost = mean-entry-size * drop-in-hit-rate
</pre>

If we model the hit distribution as a simple power-law then:

<pre>
 P(hit n'th element) ~ 1/n^{alpha"/>
</pre>

 In the continuous limit this means the total hit rate is proportional to the integral of this distribution function over the elements in
 the cache. The change in hit rate due to an eviction is then the integral of this distribution function between the initial size and
 the final size.  Assuming that the eviction size is small compared to the overall cache size we can model this as:

<pre>
 drop ~ access * 1/x^{alpha} * Delta(x)
</pre>

 Where 'access' is the overall access rate (hits + misses) and x is a unit-less measure of the 'fill level' of the cache.  Approximating the fill level as the
 ratio of hit rate to access rate, and substituting in to the eviction-cost expression we get:

<pre>
 eviction-cost = mean-entry-size * access * 1/(hits/access)^{alpha} * (eviction / (byteSize / (hits/access)))
</pre>

 Simplifying:

<pre>
 eviction-cost = (byteSize / countSize) * access * 1/(h/A)^{alpha} * (eviction * hits)/(access * byteSize)
 eviction-cost = (eviction * hits) / (countSize * (hits/access)^{alpha})
</pre>

 Removing the common factor of 'eviction' which is the same in all caches lead us to evicting from the cache with the minimum value of:

<pre>
 eviction-cost = (hits / countSize) / (hits/access)^{alpha"/>
</pre>

 When a cache has a zero hit-rate (it is in a pure loading phase) we deviate from this algorithm and allow the cache to occupy 1/n'th of the pool space where 'n'
 is the number of caches using the pool.  Once the cache starts to be accessed we re-adjust to match the actual usage pattern of that cache.
 todo: chris - what is the performance overhead of sizeOf and do we want to enable a sampling based approach?
 we're actively investigating performance issues at the moment, once we have data that indicates that a sampling based approach, either in the sizeof engine, or in
 the pooling code is our main bottleneck we'll obviously move on the relevant front.

### Pinning of Caches and Elements in Memory (2.5 and higher)

#### Pinning of Caches
Caches may be pinned using the new pinning sub-element:

<pre>
  &lt;cache name="pinnedCache"
 	timeToLiveSeconds="100"
 	&lt;pinning store="localHeap | localMemory | inCache" /&gt;
  &lt;/cache&gt;
</pre>

Pinning means that cache Elements are never evicted due to space. The cache will continue to grow as elements are added to it.

Elements will not be evicted unless the Elements have expired.

Pinning is possible at three different levels:

*   localHeap - retain the elements in the local heap
*   localMemory - retain the elements in either the local heap or the local OffHeap store, depending on what stores there are and how much
              is space is available in each.
*   inCache - retain the elements in the cache. This allows further off loading to either the DiskStore in a standalone cache, or the
         	L2 in a Terracotta backed Distributed Ehcache.

While elements in a pinned cache are guaranteed to stick in the configured level, they can nevertheless end up in other levels as well.
Eg: elements added to a cache pinned in memory can also end up on disk if a disk store has been configured.
The recommended use is reference data, where you always want the whole dataset in memory.

Pinning cannot be used with either maxEntriesLocalHeap or maxBytesLocalHeap - it is unbounded.

<blockquote>
<strong>Caution</strong>
It is possible to cause an OutOfMemory error with pinned caches. They may even look like a memory leak in the application.
They are meant to be a convenience. They should not be used with potentially unbounded data sets.
</blockquote>

#### Pinning of Elements

Some APIs like OpenJPA and Hibernate require pinning of specific Elements.

A new method on Element, Element.setPinned(true|false) has been added. When a pinned Element is placed in the cache
it will not be evicted from the Local Heap store.

## Cache Warming for multi-tier Caches 
**(Ehcache 2.5 and higher)**

When a cache starts up, the On-Heap and Off-Heap stores are always empty. Ehcache provides a BootstrapCacheLoader
mechanism to overcome this. The BootstrapCacheLoader is run before the cache is set to alive. If synchronous, loading
completes before the CacheManager starts, or if asynchronous, the CacheManager starts but loading continues agressively
rather than waiting for elements to be requested, which is a lazy loading approach.

Replicated caches provide a boot strap mechanism which populates them. For example following is the JGroups bootstrap
cache loader:

<pre>
&lt;bootstrapCacheLoaderFactory
class="net.sf.ehcache.distribution.jgroups.JGroupsBootstrapCacheLoaderFactory"
properties="bootstrapAsynchronously=true"/&gt;
</pre>

We have two new bootstrapCacheLoaderFactory implementations: one for standalone caches with DiskStores, and one for
	Terracotta Distributed caches.

### DiskStoreBootstrapCacheLoaderFactory

The DiskStoreBootstrapCacheLoaderFactory loads elements from the DiskStore to the On-Heap Store and the Off-Heap store
until either:

* the memory stores are full
* the DiskStore has been completely loaded

#### Configuration

The DiskStoreBootstrapCacheLoaderFactory is configured as follows:

<pre>
&lt;bootstrapCacheLoaderFactory
class="net.sf.ehcache.store.DiskStoreBootstrapCacheLoaderFactory"
properties="bootstrapAsynchronously=true"/&gt;
</pre>

### TerracottaBootstrapCacheLoaderFactory

The TerracottaBootstrapCacheLoaderFactory loads elements from the Terracotta L2 to the L1 based on what it was using
the last time it ran. If this is the first time it has been run it has no effect.

It works by periodically writing the keys used by the L1 to disk.

#### Configuration

The TerracottaStoreBootstrapCacheLoaderFactory is configured as follows:

<pre>&lt;bootstrapCacheLoaderFactory class="net.sf.ehcache.terracotta.TerracottaBootstrapCacheLoaderFactory"
   properties="bootstrapAsynchronously=true,
               directory=dumps,
               interval=5,
               immediateShutdown=false,
               snapshotOnShutDown=true,
               doKeySnapshot=false,
               doKeySnapshotOnDedicatedThread=false"/&gt;
</pre>

The configuration properties are:

* bootstrapAsynchronously: Whether to bootstrap asynchronously or not.
  Asynchronous bootstrap will allow the cache to start up for use while loading continues.
* directory: The directory that snapshots are created in.
  By default this will use the CacheManager's DiskStore path.
* interval: Interval in seconds between each key snapshot. Default is every 10 minutes (600 seconds).
 Cache performance overhead increases with more frequent snapshots and is dependent on such factors as cache size and disk speed. Thorough testing with various values is highly recommended.
* immediateShutdown: Whether, when shutting down the Cache, it should let the keysnapshotting
 (if in progress) finish or terminate right away. Defaults to true.
* snapshotOnShutDown: Whether to take the local key-set snapshot when the Cache is disposed. Defaults to false.
* doKeySnapshot : Set to false to disable keysnapshotting. Default is true.
 Enables loading from an existing snapshot without taking new snapshots after the existing one been loaded (stable snapshot). Or to only snapshot at cache disposal (see snapshotOnShutdown).
* useDedicatedThread : By default, each CacheManager uses a thread pool of 10 threads to do the snapshotting. If you want the cache to use a dedicated thread for the snapshotting, set this to true

Key snapshots will be in the diskstore directory configured at the cachemanager level.

One file is created for each cache with the name '&lt;cacheName&gt;'.keySet.

In case of a abrupt termination, while new snapshots are being written they are written using the extension `.temp`
and then after the write is complete the existing file is renamed to .old, the .temp is renamed to .keyset and finally
the .old file is removed. If an abrupt termination occurs you will see some of these files in the directory which will
be cleaned up on the next startup.

Like other DiskStore files, keyset snapshot files can be migrated to other nodes for warmup.

If between restarts, the cache can't hold the entire hot set locally, the Loader will stop loading as soon as the on-heap (or off-heap)
store has been filled.

## copyOnRead and copyOnWrite cache configuration

A cache can be configured to copy the data, rather than return reference to it on get or put. This is configured using the
`copyOnRead` and `copyOnWrite` attributes of cache and defaultCache elements in your configuration or programmatically as follows:

<pre>
CacheConfiguration config = new CacheConfiguration("copyCache", 1000).copyOnRead(true).copyOnWrite(true);
Cache copyCache = new Cache(config);
</pre>

The default configuration will be false for both options.

In order to copy elements on put()-like and/or get()-like operations, a CopyStrategy is being used. The default implementation
uses serialization to copy elements. You can provide your own implementation of `net.sf.ehcache.store.compound.CopyStrategy` like
this:

<pre>
&lt;cache name="copyCache"
      maxEntriesLocalHeap="10"
      eternal="false"
      timeToIdleSeconds="5"
      timeToLiveSeconds="10"
      overflowToDisk="false"
      copyOnRead="true"
      copyOnWrite="true"&gt;
   &lt;copyStrategy class="com.company.ehcache.MyCopyStrategy"/&gt;
&lt;/cache&gt;
</pre>

Per cache, a single instance of your `CopyStrategy` is used, hence your implementation of CopyStrategy.copy(T): T has to be thread-safe.

## Special System Properties

<a id="net.sf.ehcache.disabled"></a>
### net.sf.ehcache.disabled 
Setting this System Property to `true` disables caching in ehcache. If disabled no elements will be added to a cache.
i.e. puts are silently discarded.
 e.g. `java -Dnet.sf.ehcache.disabled=true` in the Java command line.

<a id="net.sf.ehcache.use.classic.lru"></a>
### net.sf.ehcache.use.classic.lru
 Set this System property to `true` to use the older LruMemoryStore implementation
 when LRU is selected as the eviction policy.
 This is provided for ease of migration.
 e.g. `java -Dnet.sf.ehcache.use.classic.lru=true` in the Java command line.

## ehcache.xsd
Ehcache configuration files must be comply with the Ehcache XML schema, ehcache.xsd.
It can be downloaded from [http://ehcache.org/ehcache.xsd](http://ehcache.org/ehcache.xsd).

## ehcache-failsafe.xml
If the CacheManager default constructor or factory method is called, Ehcache looks for a
  file called ehcache.xml in the top level of the classpath. Failing that it looks for
  ehcache-failsafe.xml in the classpath. ehcache-failsafe.xml is packaged in the Ehcache jar
  and should always be found.

ehcache-failsafe.xml provides an extremely simple default configuration to enable users to
get started before they create their own ehcache.xml.

If it used Ehcache will emit a warning, reminding the user to set up a proper configuration.
The meaning of the elements and attributes are explained in the section on ehcache.xml.

<pre>
&lt;ehcache&gt;
&lt;diskStore path="java.io.tmpdir"/&gt;
&lt;defaultCache
       maxEntriesLocalHeap="10000"
       eternal="false"
       timeToIdleSeconds="120"
       timeToLiveSeconds="120"
       overflowToDisk="true"
       maxEntriesLocalDisk="10000000"
       diskPersistent="false"
       diskExpiryThreadIntervalSeconds="120"
       memoryStoreEvictionPolicy="LRU"
       /&gt;
&lt;/ehcache&gt;
</pre>

## Update Checker

The update checker is used to see if you have the latest version of Ehcache. It is also used
to get non-identifying feedback on the OS architectures using Ehcache.
To disable the check, do one of the following:

### By System Property

<pre>
-Dnet.sf.ehcache.skipUpdateCheck=true
</pre>

### By Configuration

The outer `ehcache` element takes an `updateCheck` attribute, which is set to false as in the
following example.

<pre>
&lt;ehcache xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xsi:noNamespaceSchemaLocation="ehcache.xsd"
        updateCheck="false" monitoring="autodetect"
        dynamicConfig="true"&gt;
</pre>

## ehcache.xml and other configuration files

Prior to ehcache-1.6, Ehcache only supported ASCII ehcache.xml configuration files.
  Since ehcache-1.6, UTF8 is supported, so that configuration can use Unicode. As UTF8 is
  backwardly compatible with ASCII, no conversion is necessary.

If the CacheManager default constructor or factory method is called, Ehcache looks for a
  file called ehcache.xml in the top level of the classpath.

The non-default creation methods allow a configuration file to be specified which can be
  called anything.

One XML configuration is required for each CacheManager that is created. It is an error to
use the same configuration, because things like directory paths and listener ports will
conflict. Ehcache will attempt to resolve conflicts and will emit a warning reminding the
user to configure a separate configuration for multiple CacheManagers with conflicting
settings.

The sample ehcache.xml is included in the Ehcache distribution. It contains full commentary required to configure each element. Further
  information can be found in specific chapters in the Guide.

It can also be downloaded from [http://ehcache.org/ehcache.xml](/ehcache.xml).