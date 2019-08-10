package util;

import java.util.Objects;

public class Pair<A, B> {
	private final A a;
	private final B b;

	public Pair(A a, B b) {
		this.a = a;
		this.b = b;
	}

	public static <A, B> Pair<A, B> valueOf(A first, B second) {
		return new Pair<A, B>(first, second);
	}

	public static Pair<Integer, Double> valueOf(int first, double second) {
		return new Pair<>(Integer.valueOf(first), Double.valueOf(second));
	}

	public A getFirst() {
		return a;
	}

	public B getSecond() {
		return b;
	}

	@Override
	public int hashCode() {
		return Objects.hash(a, b);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		Pair<?, ?> other = (Pair<?, ?>) obj;
		return Objects.equals(a, other.a) && Objects.equals(b, other.b);
	}

	@Override
	public String toString() {
		return (a == null ? "null" : a.toString()) + ", " + (b == null ? "null" : b.toString());
	}
}
