RichWPS ModelBuilder for orchestrating OWS processes.
This library is part of the BMBF research project RichWPS.

The ModelBuilder is a graphical tool for easy OWS orchestration. It centers around the OGC Web Processing Service.

# Usage

1. build [RichWPS commons](https://github.com/richwps/commons)
2. build [SemanticProxyClient](https://github.com/richwps/semanticproxy)
3. build [MonitorClient](https://github.com/richwps/RichWPS-MonitorClient)
4. `mvn clean install`

# Development

* `mvn javadoc:javadoc cobertura:cobertura pmd:pmd pmd:cpd site:site -Ddependency.locations.enabled=false`
* `note` some unit-tests might fail during development. They depend on running servers.

# Contents

* `ModlBuilder::1.0.0-SNAPSHOT`


# License

Not, yet.