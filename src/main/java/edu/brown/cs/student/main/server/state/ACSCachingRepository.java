package edu.brown.cs.student.main.server.state;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import kotlin.Pair;

/**
 * Initializes a new instance of the ACSRepositoryInterface with an additional layer of caching
 * responses to avoid re-requesting for recently requested data. The cache automatically handles
 * loading of new data as required and can be configured with custom expiration policies.
 */

public class ACSCachingRepository implements ACSRepositoryInterface {

  ACSRepositoryInterface wrappedRepo;
  private LoadingCache<Pair<String, String>, List<String>> cache;

  /**
   * Constructs ACSCachingRepository with a specified underlying other ACSRepositoryInterface implementation,
   * cache size, expiration time, and time unit for expiration to use in constructing the cache. The
   * cache is set up to automatically fetch data using the provided repository if it does not exist
   * in the cache or if the data has expired according to the specified policies.
   *
   * @param toWrap   The ACSRepositoryInterface instance that this caching repository will wrap.
   * @param size     The maximum number of entries the cache can hold.
   * @param numUnits The number of units after which a cache entry will expire.
   * @param unitType The TimeUnit that specifies the unit for numUnits.
   */

  public ACSCachingRepository(
      ACSRepositoryInterface toWrap, int size, int numUnits, TimeUnit unitType) {
    this.wrappedRepo = toWrap;

    this.cache =
        CacheBuilder.newBuilder()
            .maximumSize(size)
            .expireAfterWrite(numUnits, unitType)
            .recordStats()
            .build(
                // strategy pattern taken from class livecode
                new CacheLoader<>() {
                  @Override
                  public List<String> load(Pair<String, String> loc)
                      throws URISyntaxException, IOException, InterruptedException {
                    return wrappedRepo.fetch(loc.getFirst(), loc.getSecond());
                  }
                });
  }

  /**
   * Fetches data for the user queried state, county pair and uses the cache to avoid unnecessary
   * fetch operations to the underlying data source.
   * @param state   The name of the state for which data is to be fetched.
   * @param county  The name of the county within the specified state for which data is to be fetched.
   * @return A list containing the fetched data. The contents of the list depend on the underlying
   *         repository implementation but typically include data like percentages, dates, and times.
   */

  @Override
  public List<String> fetch(String state, String county) {
    List<String> result = cache.getUnchecked(new Pair<>(state, county));
    return result;
  }
}
