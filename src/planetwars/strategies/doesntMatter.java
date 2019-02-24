//package planetwars.strategies;
//
//import planetwars.publicapi.IEdge;
//import planetwars.publicapi.IVisiblePlanet;
//
//public class doesntMatter
//        else{ //****************************//
//            for(
//    IVisiblePlanet planet : conqueredVisiblePlanets) {
//
//        // If theres a planet next to a enemy... attack enemy if you have more than 10% population than them
//        //NEW CONDITION
//        if(sourceWithEnemyPlanet.contains(planet) && enemyPlanet.size() >= friendlyPlanet.size()) {
//            System.out.println("HELLOMOM");
//            for(IEdge edge : planet.getEdges()) {
//                //IF statement here to check if its by the enemy or friendly
//                //ePlanet returns false because it edge lines up with friendly
//                IVisiblePlanet ePlanet = getKeyByValue(enemyPlanet, edge.getDestinationPlanetId());
//                if(planet.getPopulation() > ePlanet.getPopulation() * 1.25) { // inbetwween value?
//                    sourcePlanet = planet;
//                    destinationPlanet = ePlanet;
//                    popSend = (long) (ePlanet.getPopulation() * 1.2);
//                    //break;
//                    //if less population than enemy planet * 1.1
//                } else if(planet.getPopulation() < ePlanet.getPopulation() * 1.25) {
//                    for(IEdge edge2 : ePlanet.getEdges()) {
//                        //if connected to a friendly planet,
//                        if(friendlyPlanet.containsValue(edge2.getDestinationPlanetId())) {
//                            IVisiblePlanet planet1 = getKeyByValue(friendlyPlanet, edge2.getDestinationPlanetId());
//                            if(planet1 != planet) {
//                                // this can't happen because we already know its next to a friendly planet not enemy
//                                if(.85 * planet1.getPopulation() > 1.2 * ePlanet.getPopulation() - planet.getPopulation()) {
//                                    sourcePlanet = planet;
//                                    destinationPlanet = ePlanet;
//                                    popSend = planet.getPopulation() - 1;
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
//        //when will our planet's get below or equal 0 pop? So this will always run
//        else if(planet.getPopulation() > 20 && friendlyPlanet.size() > enemyPlanet.size()) {
//            //CONDITION HERE OR AND ABOVE
//            System.out.println("LOLOLOL");
//            for(IEdge edge : planet.getEdges()) {
//                if(friendlyPlanet.containsValue(edge.getDestinationPlanetId())) {
//                    //get IVisible planet
//                    IVisiblePlanet checkPop = getKeyByValue(friendlyPlanet, edge.getDestinationPlanetId());
//                    long population = planet.getPopulation();
//                    count++;
//                    //...//
//                    if(count == planet.getEdges().size()) {
//                        break;
//                    } else {
//                        if (population > popCheck) {
//                            destinationPlanet = checkPop;
//                            sourcePlanet = planet;
//                            popCheck = population;
//                            popSend = sourcePlanet.getPopulation() - 1;
//                        }
//                    }
//                }
//
//            }
//        }
//
//
//        // if Friendly planet is huge af and next to [ONLY] friendlies , send almost all troops over to other friendly
//        // If by multiple friendlies, picks the second friendly to send troops to
//        else if(planet.getPopulation() >= planet.getSize() * 2/3 && sourceWithFriendly.contains(planet)) {
//            System.out.println("Whats UP?");
//            sourcePlanet = planet;
//            double index = 10000000;
//            if(sourceWithFriendly.contains(planet)) {
//                for(IEdge edge : planet.getEdges()) {
//                    IVisiblePlanet friend = getKeyByValue(friendlyPlanet, edge.getDestinationPlanetId());
//                    if(friend.getPopulation() < index) {
//                        destinationPlanet = friend;
//                        popSend = planet.getPopulation() - 1;
//                        index = friend.getPopulation();
//                    }
//                }
//
//            }
//            //If planet is next to a enemy send 2/3 of army to that planet
//
//            else if(sourceWithEnemyPlanet.contains(planet)) {
//                System.out.println("YOLO");
//                for (IEdge edge : planet.getEdges()) {
//                    if (enemyPlanet.containsValue(edge.getDestinationPlanetId())) {
//                        IVisiblePlanet enemy = getKeyByValue(enemyPlanet, edge.getDestinationPlanetId());
//                        if(enemy.getPopulation() < index) {
//                            destinationPlanet = enemy;
//                            popSend = 2/3 * planet.getPopulation();
//                            index = enemy.getPopulation();
//                        }
//                    }
//                }
//            }
//        }
//    }
//} {
//}
