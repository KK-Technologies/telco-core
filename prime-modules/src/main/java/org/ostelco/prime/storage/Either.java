package org.ostelco.prime.storage;

import java.util.function.Function;

/**
 * Simple Either implementation for error handling
 * @param <L> Left type (typically error)
 * @param <R> Right type (typically success value)
 */
public abstract class Either<L, R> {
    
    public abstract boolean isLeft();
    public abstract boolean isRight();
    public abstract L getLeft();
    public abstract R getRight();
    
    public static <L, R> Either<L, R> left(L value) {
        return new Left<>(value);
    }
    
    public static <L, R> Either<L, R> right(R value) {
        return new Right<>(value);
    }
    
    public <T> Either<L, T> map(Function<R, T> mapper) {
        if (isRight()) {
            return right(mapper.apply(getRight()));
        } else {
            return left(getLeft());
        }
    }
    
    public <T> Either<T, R> mapLeft(Function<L, T> mapper) {
        if (isLeft()) {
            return left(mapper.apply(getLeft()));
        } else {
            return right(getRight());
        }
    }
    
    private static class Left<L, R> extends Either<L, R> {
        private final L value;
        
        Left(L value) {
            this.value = value;
        }
        
        @Override
        public boolean isLeft() {
            return true;
        }
        
        @Override
        public boolean isRight() {
            return false;
        }
        
        @Override
        public L getLeft() {
            return value;
        }
        
        @Override
        public R getRight() {
            throw new RuntimeException("Cannot get right value from Left");
        }
    }
    
    private static class Right<L, R> extends Either<L, R> {
        private final R value;
        
        Right(R value) {
            this.value = value;
        }
        
        @Override
        public boolean isLeft() {
            return false;
        }
        
        @Override
        public boolean isRight() {
            return true;
        }
        
        @Override
        public L getLeft() {
            throw new RuntimeException("Cannot get left value from Right");
        }
        
        @Override
        public R getRight() {
            return value;
        }
    }
}