```mermaid
sequenceDiagram
participant U as User
participant GUI as Grapher UI (HTTP)
participant GC as Grapher Chart (UDP)
participant RC as Robot Control (HTTP)
participant RD as Robot Data (UDP)

GUI->>RC: GET /v1/grapher/inventory
U->>GUI: Selects measurements
U->>GUI: Start

GUI->>RC: POST /v1/grapher/subscription
RC->>+RD: Start
RD-->>+GC: Streaming Data
U->>GUI: Stop
GUI->>RC: DELETE /v1/grapher/subscription
RC->>-RD: Stop
deactivate GC

```
