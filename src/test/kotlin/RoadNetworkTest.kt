import org.junit.Test
import org.junit.Assert.assertEquals
import traffic_simulation.RoadNetwork
import traffic_simulation.Vehicle

class RoadNetworkTest {

    @Test
    fun calculateDemandAtTimestep_DemandOfOneVehicleInFirstHour_demandIsTheVehiclesDemand() {
        val road = RoadNetwork(20)

        val BMW: Vehicle = Vehicle(1, mutableListOf(1, 4, 5, 8))

        val cars: List<Vehicle> = listOf(BMW)

        val calculated = road.calculateDemandAtTimestep(cars , 1)

        assertEquals(1, calculated)
    }

    @Test
    fun calculateDemandAtTimestep_DemandOfOneVehicleInLastHour_demandIsTheVehiclesDemand() {
        val road = RoadNetwork(20)

        val BMW: Vehicle = Vehicle(1, mutableListOf(1, 4, 5, 8, 24))

        val cars: List<Vehicle> = listOf(BMW)

        val calculated = road.calculateDemandAtTimestep(cars , 24)

        assertEquals(1, calculated)
    }

    @Test
    fun calculateDemandAtTimestep_DemandOfNoneVehicle_thereIsNoDemand() {
        val road = RoadNetwork(20)

        val cars: List<Vehicle> = listOf()

        val calculated = road.calculateDemandAtTimestep(cars , 5)

        assertEquals(0, calculated)
    }

    @Test
    fun calculateDemand_DemandOfSeveralVehicles_demandIsTheSummedVehiclesDemand() {
        val road = RoadNetwork(20)

        val BMW: Vehicle = Vehicle(1, mutableListOf(3, 4, 5, 8, 24))
        val AUDI: Vehicle = Vehicle(2, mutableListOf(3, 4, 5, 8, 9, 10))
        val VOLVO: Vehicle = Vehicle(3, mutableListOf(3, 4, 5, 8))

        val cars: List<Vehicle> = listOf(BMW, AUDI, VOLVO)

        val calculated = road.calculateDemandAtTimestep(cars , 5)

        assertEquals(3, calculated)
    }

    @Test
    fun calculateDemand_DemandOfSeveralVehiclesNotInOrder_demandIsTheSummedVehiclesDemand() {
        val road = RoadNetwork(20)

        val BMW: Vehicle = Vehicle(1, mutableListOf(5, 8, 24, 3))
        val AUDI: Vehicle = Vehicle(2, mutableListOf(8, 3, 4, 5, 9, 10))
        val VOLVO: Vehicle = Vehicle(3, mutableListOf(8, 4, 3, 5, 8))

        val cars: List<Vehicle> = listOf(BMW, AUDI, VOLVO)

        val calculated = road.calculateDemandAtTimestep(cars , 5)

        assertEquals(3, calculated)
    }

    @Test
    fun calculateDemand_SeveralCarsWithoutDemands_NoDemand() {
        val road = RoadNetwork(20)

        val BMW: Vehicle = Vehicle(1, mutableListOf())
        val AUDI: Vehicle = Vehicle(2, mutableListOf())
        val VOLVO: Vehicle = Vehicle(3, mutableListOf())

        val cars: List<Vehicle> = listOf(BMW, AUDI, VOLVO)

        val calculated = road.calculateDemandAtTimestep(cars, 24)

        assertEquals(0, calculated)
    }

    @Test
    fun isTrafficJam_capacityBiggerThanDemand_noTrafficJam() {
        val road = RoadNetwork(20)

        val trafficJam: Boolean = road.isTrafficJam(3)
        assertEquals(false, trafficJam)
    }

    @Test
    fun isTrafficJam_noCapacityCemand_noTrafficJam() {
        val road = RoadNetwork(20)

        val trafficJam: Boolean = road.isTrafficJam(0)
        assertEquals(false, trafficJam)
    }

    @Test
    fun isTrafficJam_capacitySmallerThanDemand_trafficJamdHappens() {
        val road = RoadNetwork(2)

        val trafficJam: Boolean = road.isTrafficJam(3)

        assertEquals(true, trafficJam)
    }

    @Test
    fun isTrafficJam_capacityEqualsDemand_noTrafficJam() {
        val road = RoadNetwork(5)

        val trafficJam: Boolean = road.isTrafficJam(5)

        assertEquals(false, trafficJam)
    }

    @Test
    fun isTrafficJam_capacityAndDemandZero_noTrafficJam() {
        val road = RoadNetwork(0)

        val trafficJam: Boolean = road.isTrafficJam(0)

        assertEquals(false, trafficJam)
    }

    @Test
    fun calculateDemandForTimestepAndCauseTrafficJam_RoadWithoutCapacity_everyDemandDelaysVehicle() {
        val road = RoadNetwork(0)
        val timestep = 8

        val car1: Vehicle = Vehicle(1, mutableListOf(3, 4, 5, 8, 9, 10, 11))
        val car2: Vehicle = Vehicle(2, mutableListOf(3, 4, 5, 8, 9, 10, 11))
        val car3: Vehicle = Vehicle(3, mutableListOf(3, 4, 5, 8, 9, 10, 11))
        val car4: Vehicle = Vehicle(4, mutableListOf(3, 4, 5, 8, 9, 10, 11))
        val car5: Vehicle = Vehicle(5, mutableListOf(3, 4, 5, 8))
        val car6: Vehicle = Vehicle(6, mutableListOf(3, 4, 5, 8))

        val allVehicles: List<Vehicle> = listOf(car1, car2, car3, car4, car5, car6)

        road.calculateDemandForTimestepAndCauseTrafficJam(timestep , allVehicles)

        for (vehicle in allVehicles){
            val test : Boolean = vehicle.delayedInHours.contains(8)
            assertEquals(true, test)
        }
    }

    @Test
    fun simulateScenario_vehiclesDemandTooMuchCapacityInLastTimestep_NumberOfDelaysIsCorrect() {
        val road = RoadNetwork(2)

        val car1: Vehicle = Vehicle(1, mutableListOf(24))
        val car2: Vehicle = Vehicle(2, mutableListOf(24))
        val car3: Vehicle = Vehicle(3, mutableListOf(24))
        val car4: Vehicle = Vehicle(4, mutableListOf(24))
        val car5: Vehicle = Vehicle(5, mutableListOf(24))
        val car6: Vehicle = Vehicle(6, mutableListOf(24))

        val allVehicles: List<Vehicle> = listOf(car1, car2, car3, car4, car5, car6)
        val output : List<Vehicle> = road.simulateScenario(allVehicles)

        var i: Int = 0

        for (car in output) {
            for (hour in car.delayedInHours) {
                i = i + 1
            }
        }
        assertEquals(6, i)
    }

    @Test
    fun simulateScenario_vehiclesDemandTooMuchCapacityInOneTimestep_delaysGoThroughAllTimestepsSinceThen() {
        val road = RoadNetwork(2)

        val car1: Vehicle = Vehicle(1, mutableListOf(19))
        val car2: Vehicle = Vehicle(2, mutableListOf(19))
        val car3: Vehicle = Vehicle(3, mutableListOf(19))

        val allVehicles: List<Vehicle> = listOf(car1, car2, car3)
        val output : List<Vehicle> = road.simulateScenario(allVehicles)

        var i: Int = 0

        for (car in output) {
            for (hour in car.delayedInHours) {
                i = i + 1
            }
        }
        assertEquals(18, i)
    }

    @Test
    fun simulateScenario_vehiclesDemandTooMuchCapacityInSeveralTimesteps_delaysGoThroughAllTimestepsSinceThen() {
        val road = RoadNetwork(3)

        val car1: Vehicle = Vehicle(1, mutableListOf(3, 4, 5, 8, 9, 10, 11))
        val car2: Vehicle = Vehicle(2, mutableListOf(3, 4, 5, 8, 9, 10, 11))
        val car3: Vehicle = Vehicle(3, mutableListOf(3, 4, 5, 8, 9, 10, 11))
        val car4: Vehicle = Vehicle(4, mutableListOf(3, 4, 5, 8, 9, 10, 11))
        val car5: Vehicle = Vehicle(5, mutableListOf(3, 4, 5, 8))
        val car6: Vehicle = Vehicle(6, mutableListOf(3, 4, 5, 8))

        val allVehicles: List<Vehicle> = listOf(car1, car2, car3, car4, car5, car6)
        val output : List<Vehicle> = road.simulateScenario(allVehicles)

        var i: Int = 0

        for (car in output) {
            for (hour in car.delayedInHours) {
                i = i + 1
            }
        }
        assertEquals(132, i)
    }

    @Test
    fun simulateScenario_noVehiclesGettingDelayed_NumberOfDelaysIsCorrect() {
        val road = RoadNetwork(50)

        val car1: Vehicle = Vehicle(1, mutableListOf(3, 4, 5, 8, 9, 10, 11))
        val car2: Vehicle = Vehicle(2, mutableListOf(3, 4, 5, 8, 9, 10, 11))
        val car3: Vehicle = Vehicle(3, mutableListOf(3, 4, 5, 8, 9, 10, 11))
        val car4: Vehicle = Vehicle(4, mutableListOf(3, 4, 5, 8, 9, 10, 11))
        val car5: Vehicle = Vehicle(5, mutableListOf(3, 4, 5, 8))
        val car6: Vehicle = Vehicle(6, mutableListOf(3, 4, 5, 8))

        val allVehicles: List<Vehicle> = listOf(car1, car2, car3, car4, car5, car6)
        val output : List<Vehicle> = road.simulateScenario(allVehicles)

        var i: Int = 0

        for (car in output) {
            for (hour in car.delayedInHours) {
                i = i + 1
            }
        }
        assertEquals(0, i)
    }

    @Test
    fun simulateScenario_demandMatchesCapacity_noDelays() {
        val road = RoadNetwork(6)

        val car1: Vehicle = Vehicle(1, mutableListOf(3, 4, 5, 8, 9, 10, 11))
        val car2: Vehicle = Vehicle(2, mutableListOf(3, 4, 5, 8, 9, 10, 11))
        val car3: Vehicle = Vehicle(3, mutableListOf(3, 4, 5, 8, 9, 10, 11))
        val car4: Vehicle = Vehicle(4, mutableListOf(3, 4, 5, 8, 9, 10, 11))
        val car5: Vehicle = Vehicle(5, mutableListOf(3, 4, 5, 8))
        val car6: Vehicle = Vehicle(6, mutableListOf(3, 4, 5, 8))
        val car7: Vehicle = Vehicle(6, mutableListOf(9,10,11))
        val car8: Vehicle = Vehicle(6, mutableListOf(9,10,11))

        val allVehicles: List<Vehicle> = listOf(car1, car2, car3, car4, car5, car6, car7, car8)
        val output : List<Vehicle> = road.simulateScenario(allVehicles)

        var i: Int = 0

        for (car in output) {
            for (hour in car.delayedInHours) {
                i = i + 1
            }
        }
        assertEquals(0, i)
    }
}
