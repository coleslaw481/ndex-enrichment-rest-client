
[jetty]: http://eclipse.org/jetty/
[maven]: http://maven.apache.org/
[java]: https://www.oracle.com/java/index.html
[git]: https://git-scm.com/

[make]: https://www.gnu.org/software/make

NDEx Enrichment REST Client
===========================

Java REST client to interact with NDEx Enrichment REST Service
which is used by [ndexsearch-rest](https://github.com/ndexbio/ndexsearch-rest)


Requirements
============

* [Java][java] 8+ **(jdk to build)**
* [Make][make] **(to build)**
* [Maven][maven] 3.0 or higher **(to build)** -- tested with 3.6

Special Java modules to install (cause we havent put these into maven central)

* [ndex-enrichment-rest-model](https://github.com/ndexbio/ndex-enrichment-rest-model) built and installed via `mvn install`



Building  
========

Commands below build this module assuming machine has [Git][git] command line tools
installed ad above Java modules have been installed.

```Bash
# In lieu of git one can just download repo and unzip it
git clone https://github.com/ndexbio/ndex-enrichment-rest-client.git

cd ndex-enrichment-rest-client
mvn install

```



COPYRIGHT AND LICENSE
=====================

TODO

Acknowledgements
================

TODO
