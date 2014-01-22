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

The main objective is fault-tolerance.

Scaling will happen as a byproduct because more hardware is available.

## Status

Prototype. See the prototype [proto-lizard](https://github.com/afs/proto-lizard).

Lizard is yet not suitable for production use.

