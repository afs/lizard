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

Lizard is aimed at medium sized systems - a few machines.

## Scale

The main objective for the initial phase is fault-tolerance.

Scaling will happen in phase 2.

## Status

Prototype.

Lizard is yet not suitable for production use.

Do not store the only copy of data in the database.  
Persistent data formats will change; there will be no
migration path other than to reload the database.
