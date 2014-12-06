Project Lizard
==============

_Project Lizard_ -- a clustered SPARQL database providing a fault-tolerant operation.

## Fault tolerant

Lizard uses several machines to provide the SPARQL service.  If any one
machine fails (e.g. hardware or software problem), then Lizard is able to
continue providing the SPARQL service.  Lizard assumes problems are
"fail-stop" -- the machine fails and stops on an error, and does not
generate malicious information.  No [Byzantine fault tolerance]
(https://en.wikipedia.org/wiki/Byzantine_fault_tolerance).

## License

[Apache License](http://www.apache.org/licenses/LICENSE-2.0)

## Status

Prototype.

Lizard is yet not suitable for production use.

Do not store the only copy of data in the database.  
Persistent data formats will change; there will be no
migration path other than to reload the database.

## Wiki

[Lizard Wiki](https://github.com/afs/lizard/wiki)

## Downloads

Currently, Lizard requires building from source.

## Build

Lizard requires Java 8.

To build Lizard:

1. Get the source: `git clone https://github.com/afs/lizard/`.
1. Build and install the related "[quack](https://github.com/afs/quack)" query library.
    1. Get the source.
    1. `mvn clean install`
1. Build.install Lizard locally: `mvn clean install`
