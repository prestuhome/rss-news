<nav class="navbar navbar-light bg-light">
    <a class="navbar-brand" href="/">News Aggregator</a>
    <form class="form-inline" action="/news"  method="get">
        <input class="form-control mr-sm-2" type="text" name="filter" value="${filter!}" placeholder="Фильтр" aria-label="Фильтр">
        <button class="btn btn-outline-success my-2 my-sm-0" type="submit">Поиск</button>
    </form>
</nav>
