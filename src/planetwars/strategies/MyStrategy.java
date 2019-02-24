/*
 *
 * Thomas Nguyen - nguy3817@umn.edu
 *                   Chad Myers CSCI1933
 * Brandon Chan - chanx411@umn.edu
 *
 */

package planetwars.strategies;

import planetwars.publicapi.IEvent;
import planetwars.publicapi.IPlanet;
import planetwars.publicapi.IPlanetOperations;
import planetwars.publicapi.IStrategy;
import planetwars.publicapi.*;


import java.util.*;

public class MyStrategy implements IStrategy{
    private long popCheck = 0;


    public void takeTurn(List<IPlanet> planets, IPlanetOperations planetOperations, Queue<IEvent> eventsToExecute) {
        List<IVisiblePlanet> conqueredVisiblePlanets = new ArrayList<>();
        List<IVisiblePlanet> unconqueredVisiblePlanets = new ArrayList<>();

        //For all planets in the planet list and make a list of all conquered planets and unconquered planets
        for (IPlanet planet : planets) {
            if (planet instanceof IVisiblePlanet && ((IVisiblePlanet) planet).getOwner() == Owner.SELF) {
                conqueredVisiblePlanets.add((IVisiblePlanet) planet);
            } else if (planet instanceof IVisiblePlanet)
                unconqueredVisiblePlanets.add((IVisiblePlanet) planet);
        }

        // Create a stack of planet objects, which holds the population, destination planet, and a source planet
        Stack<Object> myPlanets = pickPlanet(conqueredVisiblePlanets, unconqueredVisiblePlanets);
        
        long population = (long) myPlanets.pop();
        IPlanet destination = (IPlanet) myPlanets.pop();
        IPlanet source = (IPlanet) myPlanets.pop();
        eventsToExecute.offer(planetOperations.transferPeople(source, destination, population));
    }

    //Finds out if the edge of a planet is connected to an unconquered one
    //adds that planet to a list
    public List<IVisiblePlanet> checkUnconquered(List<IVisiblePlanet> conqueredVisiblePlanets, HashMap<IVisiblePlanet, Integer> planetUnconqueredDictionary){
        List<IVisiblePlanet> sourcePlanets = new ArrayList<>();

        for(IVisiblePlanet planet : conqueredVisiblePlanets) {
            for(IEdge edge : planet.getEdges()) {
                //If the edge is leading to an unconquered planet, add it to the sources list
                if (planetUnconqueredDictionary.containsValue(edge.getDestinationPlanetId())) {
                    if(!listContainsIterator(sourcePlanets, planet)) {
                        sourcePlanets.add(planet);
                    }
                }
            }
        }
        return sourcePlanets;
    }

    //Checks if an unconquered planet is owned by the enemy
    //adds that planet to a dictionary
    public HashMap<IVisiblePlanet, Integer> checkEnemyPlanet(List<IVisiblePlanet> unconqueredVisiblePlanets) {
        HashMap<IVisiblePlanet, Integer> enemyPlanets = new HashMap<>();
        Iterator<IVisiblePlanet> unconqueredIterator = unconqueredVisiblePlanets.iterator();
        while(unconqueredIterator.hasNext()) {
            IVisiblePlanet checkPlanet = unconqueredIterator.next();
            //If it is not owned by your and not unowned (so owned by enemy)
            //add it to the enemy planet list
            if(checkPlanet.getOwner() == Owner.OPPONENT) {
                enemyPlanets.put(checkPlanet, checkPlanet.getId());
            }
        }
        return enemyPlanets;
    }

    //Check is unconquered planet is neutral and creates hash map of neutral planets
    public HashMap<IVisiblePlanet, Integer> checkNeutralPlanet(List<IVisiblePlanet> unconqueredVisiblePlanets) {
        HashMap<IVisiblePlanet, Integer> neutralPlanets = new HashMap<>();
        Iterator<IVisiblePlanet> unconqueredIterator = unconqueredVisiblePlanets.iterator();
        while(unconqueredIterator.hasNext()) {
            IVisiblePlanet checkPlanet = unconqueredIterator.next();
            //If planet unowned by anyone
            if(checkPlanet.getOwner() == Owner.NEUTRAL) {
                neutralPlanets.put(checkPlanet, checkPlanet.getId());
            }
        }
        return neutralPlanets;
    }

    //Adds all conquered planets to a hash map
    public HashMap<IVisiblePlanet, Integer> checkFriendlyPlanet(List<IVisiblePlanet> conqueredVisiblePlanets) {
        HashMap<IVisiblePlanet, Integer> friendlyPlanet = new HashMap<>();
        Iterator<IVisiblePlanet> conqueredIterator = conqueredVisiblePlanets.iterator();
        while(conqueredIterator.hasNext()) {
            IVisiblePlanet checkPlanet = conqueredIterator.next();
            friendlyPlanet.put(checkPlanet, checkPlanet.getId());

        }
        return friendlyPlanet;
    }

    // Looks through all conquered planets and adds it to SourceWithFriendly if the planet is next to atleast one Friendly planet and only friendlies
    public List<IVisiblePlanet> sourceWithFriendly(List<IVisiblePlanet> conqueredVisiblePlanets, HashMap<IVisiblePlanet, Integer> enemyPlanet, HashMap<IVisiblePlanet, Integer> neutralPlanet) {
        List<IVisiblePlanet> sourceWithFriendly = new ArrayList<>();
        for(IVisiblePlanet planet : conqueredVisiblePlanets) {
            for(IEdge edge : planet.getEdges()) {
                if(enemyPlanet.containsValue(edge.getDestinationPlanetId()) || neutralPlanet.containsValue(edge.getDestinationPlanetId())) {
                    continue;
                } else {
                    sourceWithFriendly.add(planet);
                }
            }
        }
        return sourceWithFriendly;
    }

    //returns two lists of planets
    //One list is planets with at least 1 enemy planet adjacent
    //The other is a list of planets that have at least 1 neutral planet adjacent, but no enemy planets
    public Object[] separatePlanets(List<IVisiblePlanet> unconqueredVisiblePlanets, List<IVisiblePlanet> conqueredVisiblePlanets, HashMap<IVisiblePlanet, Integer> planetUnconqueredDictionary) {
        List<IVisiblePlanet> sourcePlanets = checkUnconquered(conqueredVisiblePlanets, planetUnconqueredDictionary);
        HashMap<IVisiblePlanet, Integer> enemyPlanets = checkEnemyPlanet(unconqueredVisiblePlanets);

        Object[] ourPlanets = new Object[2];

        List<IVisiblePlanet> sourceWithEnemyPlanet = new ArrayList<>();
        List<IVisiblePlanet> sourceWithoutEnemyPlanet = new ArrayList<>();
        for(IVisiblePlanet planet : sourcePlanets) {
            for(IEdge sourceEdge : planet.getEdges()) {
                int theEdge = sourceEdge.getDestinationPlanetId();
                if(enemyPlanets.containsValue(theEdge)) {
                    sourceWithEnemyPlanet.add(planet);
                } else {
                    sourceWithoutEnemyPlanet.add(planet);
                }
            }
        }
        ourPlanets[0] = sourceWithEnemyPlanet;
        ourPlanets[1] = sourceWithoutEnemyPlanet;

        return ourPlanets;
    }

    public HashMap<IVisiblePlanet, Integer> allPlanets(List<IVisiblePlanet> unconqueredVisiblePlanets,  List<IVisiblePlanet> conqueredVisiblePlanets) {
        HashMap<IVisiblePlanet, Integer> allPlanets = new HashMap<>();
        for(IVisiblePlanet planet : conqueredVisiblePlanets) {
            allPlanets.put(planet, planet.getId());
        }
        for(IVisiblePlanet planet : unconqueredVisiblePlanets) {
            allPlanets.put(planet, planet.getId());
        }
        return allPlanets;
    }

    // This iterates through a list to find if a specified planet is in a particular list
    public boolean listContainsIterator(List<IVisiblePlanet> list, IVisiblePlanet planet) {
        Iterator<IVisiblePlanet> i = list.iterator();
        while(i.hasNext()) {
            IVisiblePlanet compare = i.next();
            if(planet == compare) {
                return true;
            }
        }
        return false;
    }

    // Overall picks which action our planets take and returns the population, destination planet, and source planet to a stack
    public Stack<Object> pickPlanet(List<IVisiblePlanet> conqueredVisiblePlanets, List<IVisiblePlanet> unconqueredVisiblePlanets) {
        //Creates dictionaries for conquered planets and unconquered planets and their IDs as the value
        HashMap<IVisiblePlanet, Integer> planetConqueredDictionary = new HashMap<>();
        HashMap<IVisiblePlanet, Integer> planetUnconqueredDictionary = new HashMap<>();

        //adds all conquered planets to the dictionary
        //IVisible - not mean all planets in game, planets adjacent
        for(IVisiblePlanet planets : conqueredVisiblePlanets) {
            planetConqueredDictionary.put(planets,planets.getId());
        }

        //adds all unconquered planets to the dictionary
        for(IVisiblePlanet planetsUnconq : unconqueredVisiblePlanets) {
            planetUnconqueredDictionary.put(planetsUnconq, planetsUnconq.getId());
        }

        //Creates two lists: conquered planets with at least 1 enemy adjacent
        //and conquered planets with at least 1 neutral planet adjacent but no enemies
        Object[] ourPlanets = separatePlanets(unconqueredVisiblePlanets, conqueredVisiblePlanets, planetUnconqueredDictionary);
        List<IVisiblePlanet> sourceWithEnemyPlanet = (List<IVisiblePlanet>)ourPlanets[0];
        List<IVisiblePlanet> sourceWithoutEnemyPlanet = (List<IVisiblePlanet>)ourPlanets[1];

        //Hash map of all enemy planets
        HashMap<IVisiblePlanet, Integer> enemyPlanet = checkEnemyPlanet(unconqueredVisiblePlanets);
        //HashMap of all visible neutral planets
        HashMap<IVisiblePlanet, Integer> neutralPlanet = checkNeutralPlanet(unconqueredVisiblePlanets);
        //Hash map of all friendly planets
        HashMap<IVisiblePlanet, Integer> friendlyPlanet = checkFriendlyPlanet(conqueredVisiblePlanets);
        //Creates a list of planets whose adjacent planets are only friendly
        List<IVisiblePlanet> sourceWithFriendly = sourceWithFriendly(unconqueredVisiblePlanets, enemyPlanet, neutralPlanet);
        // hash map of all planets
        HashMap<IVisiblePlanet, Integer> allPlanets = allPlanets(unconqueredVisiblePlanets,conqueredVisiblePlanets);

        Stack<Object> myPlanets = new Stack<>();
        IVisiblePlanet sourcePlanet = null;
        IVisiblePlanet destinationPlanet = null;
        long popSend = 0;
        double count = 0;

        //Start off game by conquering as many neutral planets
        // If there are more than one neutrals
        if(neutralPlanet.size() >= 1) {
            for(IVisiblePlanet planet : conqueredVisiblePlanets) {
                for(IEdge edge : planet.getEdges()) {
                    if(neutralPlanet.containsValue(edge.getDestinationPlanetId())) {
                        IVisiblePlanet thePlanet = getKeyByValue(neutralPlanet, edge.getDestinationPlanetId());
                        // If it has shuttles going to the neutral planet, dont worry about it
                        if (thePlanet.getIncomingShuttles().size() > 0) {
                            continue;
                        } else if (thePlanet.getIncomingShuttles().size() == 0) {
                            destinationPlanet = thePlanet;
                            sourcePlanet = planet;
                            // Sends half population to the neutral planet
                            popSend = sourcePlanet.getPopulation() / 2;
                            break;
                        }
                    }
                }
            }
        }
        else{
            for(IVisiblePlanet planet : conqueredVisiblePlanets) {
                //Looks through all conquered planets and if the edge is connected to a friendlyP, moving friendly big planet's pop to front lines
                for(IEdge edge : planet.getEdges()) {
                    if (listContainsIterator(sourceWithEnemyPlanet,getKeyByValue(allPlanets, edge.getDestinationPlanetId())) && !listContainsIterator(sourceWithEnemyPlanet, planet) && planet.getPopulation() > planet.getSize() * 1 / 3) {
                        destinationPlanet = getKeyByValue(allPlanets, edge.getDestinationPlanetId());
                        sourcePlanet = planet;
                        popSend = planet.getPopulation() - 1;
                    }
                    //If an edge planet is next to an enemy and the main planet is not
                    //If planet population is max and is next to an enemy
                    else if (planet.getPopulation() >= planet.getSize()) {
                        IVisiblePlanet otherPlanet = getKeyByValue(allPlanets, edge.getDestinationPlanetId());
                        if (listContainsIterator(sourceWithEnemyPlanet, otherPlanet) && listContainsIterator(sourceWithEnemyPlanet, planet)) {
                            sourcePlanet = planet;
                            destinationPlanet = otherPlanet;
                            popSend = planet.getPopulation() - 1;
                        }

                        //if planet is bigger than the adjacent planet by 1.75 times and other planet has less than 5 population and that its next to an enemy
                        else if (planet.getPopulation() > 1.75 * otherPlanet.getPopulation() && otherPlanet.getPopulation() <= 5 && listContainsIterator(sourceWithEnemyPlanet, otherPlanet)) {
                            sourcePlanet = planet;
                            destinationPlanet = otherPlanet;
                            popSend = (long) (0.5 * (float) planet.getPopulation());


                            //else if planet is bigger than the adjacent planet by 1.75 times and the other planet is an enemy or that other planet is next to an enemy
                        } else if (planet.getPopulation() > 1.75 * otherPlanet.getPopulation() && ((enemyPlanet.containsKey(otherPlanet)) || listContainsIterator(sourceWithEnemyPlanet, otherPlanet))) {
                            sourcePlanet = planet;
                            destinationPlanet = otherPlanet;

                            //if the other planet is an enemy...
                            if(enemyPlanet.containsKey(otherPlanet)) {
                                popSend = (long) (1.75 * (float) otherPlanet.getPopulation());
                            } else if(listContainsIterator(sourceWithEnemyPlanet, otherPlanet)) {
                                popSend = planet.getPopulation() - 1;
                            }

                            //else if the planet we are comparing is next to a friendly and other planet adjacent is next to an enemy
                        } else if(listContainsIterator(sourceWithFriendly,planet) && listContainsIterator(sourceWithEnemyPlanet, otherPlanet)) {
                            sourcePlanet = planet;
                            destinationPlanet = otherPlanet;
                            popSend = planet.getPopulation() - 1;
                        }

                        //Take over planet else if
                        //if the planet we are comparing is next to an enemy and the planet we are comparing's  population is less than its max size
                    } else if(listContainsIterator(sourceWithEnemyPlanet,planet) && planet.getPopulation() < planet.getSize()) {
                        IVisiblePlanet otherEnemy = getKeyByValue(allPlanets, edge.getDestinationPlanetId());
                        //if the adjacent is an enemy...
                        if(enemyPlanet.containsKey(otherEnemy)) {
                            if(planet.getPopulation() > 1.5 * otherEnemy.getPopulation()) {
                                sourcePlanet = planet;
                                destinationPlanet = otherEnemy;

                                // if our population is greater than 50 and the other's population is less than 5...
                                if(planet.getPopulation() > 50 && otherEnemy.getPopulation() <= 5) {
                                    popSend = (long) (planet.getPopulation() * (float) 0.5);
                                }

                                // if our planet has a population greater than 50
                                else if(planet.getPopulation() > 50) {
                                    popSend = (long) (otherEnemy.getPopulation() * (float) 1.5);
                                    // otherwise send it all
                                } else {

                                    popSend = planet.getPopulation() - 1;
                                }
                            }
                        }
                    }
                }
            }
        }



        myPlanets.push(sourcePlanet);
        myPlanets.push(destinationPlanet);
        myPlanets.push(popSend);
        return myPlanets;
    }

    //Taken from StackOverflow
    //Method to get a key from a value
    public <K, V> K getKeyByValue(HashMap<K, V> map, V value) {
        K key = null;
        for(Map.Entry entry : map.entrySet()) {
            if(value.equals(entry.getValue())) {
                key = (K)entry.getKey();
                break;
            }
        }
        return key;
    }


    public String getName() {
        return "Brandon and Thomas";
    }

    public boolean compete(){
        return true;
    }


}