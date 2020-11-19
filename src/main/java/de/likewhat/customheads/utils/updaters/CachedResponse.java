package de.likewhat.customheads.utils.updaters;

/*
 *  Project: CustomHeads in CachedResponse
 *     by LikeWhat
 *
 *  created on 13.04.2019 at 19:15
 */

import lombok.Getter;

@Getter
public class CachedResponse<T> {

    private final long cacheTime;
    private final T data;

    public CachedResponse(long cacheTime, T data) {
        this.cacheTime = cacheTime;
        this.data = data;
    }
}
