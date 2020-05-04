# MSCS 710 Capping Project: Resource Monitor

## Overview

This project implements an operating system resource monitor in Java, distributing 
data via [Kafka](https://kafka.apache.org/). Any number of consumers can then consume this live metric data 
independently of one another via the Kafka server. Currently, these consumers are 
a web-based GUI built with [Flutter](https://flutter.dev/docs), and a [SQLite](https://www.sqlite.org/index.html) 
database. We currently serve CPU, CPU Core, Network, Memory and Process metrics. Link to [javadoc](https://jonathansavas.github.io/marist-mscs710-capping-project/apidocs/overview-summary.html).


## Software Flow

![alt text](https://github.com/jonathansavas/marist-mscs710-capping-project/blob/master/docs/images/software-flow.png)</br></br></br></br>


## Metrics

Fields associated with each metric type and their units [can be found here](https://jonathansavas.github.io/marist-mscs710-capping-project/apidocs/edu/marist/mscs710/metricscollector/metric/Fields.html).


## Graphical User Interface

The provided GUI is a web-based interface which provides a live view of operating system metrics.
The GUI is hosted via Python 3 [http.server](https://docs.python.org/3/library/http.server.html). This
is not recommended for production for security reasons, therefore the current implementation is only
implemented for localhost.


## Dependencies

Running the application requires that users have installed Java and Python 3 and
have added both to their PATH variable. The user's "python" command should reference
a Python 3 installation.

Building this project from source will require [Maven](http://maven.apache.org/), 
in addition to [Flutter](https://flutter.dev/docs) to build to GUI. The Flutter SDK
can be [installed here](https://flutter.dev/docs/get-started/install). Additionally,
user's must [enable Flutter web support](https://flutter.dev/docs/get-started/web).


## Releases

Packaged binaries for the project can be found at the project's [releases page](https://github.com/jonathansavas/marist-mscs710-capping-project/releases). 
A full distribution is included along with a minimal distribution for user's who want
to use an existing Kafka server.


## Configuration

The full distribution is configured to work out-of-the-box. The only recommended configuration
change is for the location of the kafka-logs. The Kafka distribution default is to store
them in a root directory /tmp. Within /resource-monitor/kafka/kafka_&lt;version&gt;/config, users can
change "zookeeper.properties" property "dataDir" and "server.properties" property "log.dirs". It
is recommended to make this a location within the application directory.

Users with an existing Kafka server should edit the "config/application.properties" files of both
"metrics-collector" and "metrics-persistence". Set the appropriate "kafkabroker" and, if there may
be a conflict with a topic already on the server, change "metricstopic".
