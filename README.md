Server
======

To run server, go to `Server/` and run `gradle run --args 'A B C D'`, where
  * `A` is the IP address on which to listen for management queries;
  * `B` is the corresponding port;
  * `C` is the IP address on which to listen for payload queries;
  * `D` is the corresponding port.

For example, running `gradle run --args '127.0.0.1 3083 127.0.0.1 3084'` starts
a server that accepts management commands on `3083` and normal queries on
`3084`.

Application
===========

To run a GUI application, go into `App/` and run `gradle run`. There are several
options available:

  * The fields on the top right are server IP, server management port, and
    server worker port.
  * X is the number of queries per invocation of a client;
  * N is the number of elements in the sorted arrays;
  * M is the number of clients running simultaneously;
  * D is the cooldown between queries, *in microseconds*.

Results
=======

Some measurements are available in the `Results/` directory.
