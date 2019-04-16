package de.mrstein.customheads.updaters;

/*
 *  Project: CustomHeads in FetchResult
 *     by LikeWhat
 *
 *  created on 13.04.2019 at 00:05
 */

public interface FetchResult<T> {

    void success(T t);

    void error(Exception exception);

}
