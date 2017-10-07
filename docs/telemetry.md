# Third Coast Telemetry
Design documentation for the Third Coast Telemetry System

## Interactions
This diagram describes the basic interations between grapher and robot.

```mermaid
sequenceDiagram
participant U as User
participant GUI as Grapher UI (HTTP)
participant GC as Grapher Chart (UDP)
participant RC as Robot Control (HTTP)
participant RD as Robot Data (UDP)

U->>GUI: Selects measurements
U->>GUI: Start

GUI->>RC: Subscribe/Start
RC->>+RD: Start
RD-->>+GC: Streaming Data
U->>GUI: Stop
GUI->>RC: Stop
RC->>-RD: Stop
deactivate GC

```
