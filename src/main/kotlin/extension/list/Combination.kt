package extension.list

fun <T> List<T>.combination(doubleDirection: Boolean = false): Collection<Pair<T, T>> {
    return this.flatMapIndexed { i, first ->
        this.subList(i + 1, this.size).flatMap { second ->
            listOfNotNull(Pair(first, second), if (doubleDirection) Pair(first, second) else null)
        }
    }
}

fun <T> List<T>.combinationList(doubleDirection: Boolean = false): Collection<List<T>> {
    return this.flatMapIndexed { i, first ->
        this.subList(i + 1, this.size).flatMap { second ->
            listOfNotNull(
                listOf(first, second),
                if (doubleDirection) listOf(first, second) else null,
            )
        }
    }
}
