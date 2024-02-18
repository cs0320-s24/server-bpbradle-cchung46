package edu.brown.cs.student.main.server.state;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import kotlin.Pair;

public class ACSCachingRepository implements ACSRepositoryInterface {

  ACSRepositoryInterface wrappedRepo;
  private LoadingCache<Pair<String, String>, List<String>> cache;

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

  @Override
  public List<String> fetch(String state, String county) {
    List<String> result = cache.getUnchecked(new Pair<>(state, county));
    return result;
  }
}
