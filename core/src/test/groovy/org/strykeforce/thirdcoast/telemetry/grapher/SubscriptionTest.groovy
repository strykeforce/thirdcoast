package org.strykeforce.thirdcoast.telemetry.grapher

import spock.lang.Specification


class SubscriptionTest extends Specification {
    def "Parse"() {
    given:
    def json = """
{
    "type": "start",
    "subscription": [
        {
            "itemId": 0,
            "measurementId": 1
        },
        {
            "itemId": 10,
            "measurementId": 2
        },
        {
            "itemId": 1,
            "measurementId": 1
        },
        {
            "itemId": 11,
            "measurementId": 2
        },
        {
            "itemId": 61,
            "measurementId": 13
        },
        {
            "itemId": 62,
            "measurementId": 14
        }
    ]
}
"""

        when:
        def sub = Subscription.RequestJson.fromJson(json)

        then:
        with(sub) {
            type == "start"
            subscription.size == 6
            subscription[0].itemId == 0
            subscription[0].measurementId == 1
            subscription[1].itemId == 10
            subscription[1].measurementId == 2
            subscription[2].itemId == 1
            subscription[2].measurementId == 1
            subscription[3].itemId == 11
            subscription[3].measurementId == 2
            subscription[4].itemId == 61
            subscription[4].measurementId == 13
            subscription[5].itemId == 62
            subscription[5].measurementId == 14
        }
    }
}
