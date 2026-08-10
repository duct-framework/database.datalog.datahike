# Duct database.datalog.datahike

[Integrant][] methods for connecting to a [Datahike][] database from
the [Duct][] framework. These methods can also be used outside Duct in
any Integrant application.

[integrant]: https://github.com/weavejester/integrant
[datahike]:  https://datahike.io/
[duct]:      https://github.com/duct-framework/duct

## Installation

Add the following dependency to your deps.edn file:

    org.duct-framework/database.datalog.datahike {:mvn/version "0.1.0-SNAPSHOT"}

Or to your Leiningen project file:

    [org.duct-framework/database.datalog.datahike "0.1.0-SNAPSHOT"]

## Usage

This library provides the `:duct.database.datalog/datahike` Integrant
key, which takes a [Datahike configuration][datahike-config] as its
value.

For example:

```edn
{:duct.database.datalog/datahike
 {:store {:backend :file, :path "db"}}
```

If the database does not exist, it is automatically created when the
key is initiated.

[datahike-config]: https://cljdoc.org/d/org.replikativ/datahike/0.8.1775/doc/getting-started/configuration

## License

Copyright © 2026 James Reeves

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
