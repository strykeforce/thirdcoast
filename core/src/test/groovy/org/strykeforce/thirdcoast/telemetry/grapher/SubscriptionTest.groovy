package org.strykeforce.thirdcoast.telemetry.grapher

import groovy.json.JsonSlurper
import okio.Buffer
import spock.lang.Specification

import java.util.function.DoubleSupplier


class SubscriptionTest extends Specification {
    def "parse request JSON"() {
    given:
    def json = """
{
    "type": "start",
    "subscription": [
        {
            "itemId": 0,
            "measurementId": "OUTPUT_VOLTAGE"
        },
        {
            "itemId": 10,
            "measurementId": "OUTPUT_CURRENT"
        },
        {
            "itemId": 1,
            "measurementId": "OUTPUT_VOLTAGE"
        },
        {
            "itemId": 11,
            "measurementId": "OUTPUT_CURRENT"
        },
        {
            "itemId": 61,
            "measurementId": "STRYKE_FORCE"
        },
        {
            "itemId": 62,
            "measurementId": "STRYKE_FORCE"
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
            subscription[0].measurementId == "OUTPUT_VOLTAGE"
            subscription[1].itemId == 10
            subscription[1].measurementId == "OUTPUT_CURRENT"
            subscription[2].itemId == 1
            subscription[2].measurementId == "OUTPUT_VOLTAGE"
            subscription[3].itemId == 11
            subscription[3].measurementId == "OUTPUT_CURRENT"
            subscription[4].itemId == 61
            subscription[4].measurementId == "STRYKE_FORCE"
            subscription[5].itemId == 62
            subscription[5].measurementId == "STRYKE_FORCE"
        }
    }

    def "generate measurements"() {
        given:
        def json = """
{
    "type": "start",
    "subscription": [
        {
            "itemId": 0,
            "measurementId": "OUTPUT_CURRENT"
        },
        {
            "itemId": 10,
            "measurementId": "OUTPUT_VOLTAGE"
        }
    ]
}
"""
        def inventory = Stub(Inventory)
        def item = Stub(Item)
        item.measurementFor(_) >>> [
                new DoubleSupplier() {
                    @Override
                    double getAsDouble() {
                        return 1.23
                    }
                },
                new DoubleSupplier() {
                    @Override
                    double getAsDouble() {
                        return 27.67
                    }
                }
        ]
        inventory.itemForId(_) >>> item
        def subscription = new Subscription(inventory, "localhost", json)
        def buffer = new Buffer()
        JsonSlurper sluper = new JsonSlurper()

        when:
        subscription.measurementsToJson(buffer)
        def result = sluper.parse(buffer.readByteArray())

        then:
        result.data[0] == 1.23
        result.data[1] == 27.67
    }

    def "generate descriptions"() {
        given:
        def json = """
{
    "type": "start",
    "subscription": [
        {
            "itemId": 0,
            "measurementId": "OUTPUT_CURRENT"
        },
        {
            "itemId": 10,
            "measurementId": "OUTPUT_VOLTAGE"
        }
    ]
}
"""
        def inventory = Stub(Inventory)
        def item = Stub(Item)
        item.measurementFor(_) >> new DoubleSupplier() {
                    @Override
                    double getAsDouble() {
                        return 1.23
                    }
                }
        item.description() >>> ["Test 1", "Test 2"]
        inventory.itemForId(_) >>> item
        def subscription = new Subscription(inventory, "localhost", json)
        def buffer = new Buffer()
        JsonSlurper sluper = new JsonSlurper()

        when:
        subscription.toJson(buffer)
        def result = sluper.parse(buffer.readByteArray())

        then:
        result.type == 'subscription'
        result.descriptions[0] == "Test 1: Output Current"
        result.descriptions[1] == "Test 2: Output Voltage"
    }

}
