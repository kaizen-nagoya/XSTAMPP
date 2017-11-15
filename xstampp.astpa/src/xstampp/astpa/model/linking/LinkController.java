/*******************************************************************************
 * Copyright (C) 2017 Lukas Balzer, Asim Abdulkhaleq, Stefan Wagner Institute of SoftwareTechnology,
 * Software Engineering Group University of Stuttgart, Germany. All rights reserved. This program
 * and the accompanying materials are made available under the terms of the Eclipse Public License
 * v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Lukas Balzer - initial API and implementation
 ******************************************************************************/
package xstampp.astpa.model.linking;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Optional;
import java.util.UUID;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import xstampp.astpa.model.service.UndoTextChange;
import xstampp.model.ObserverValue;

public class LinkController extends Observable {

  @XmlElement
  @XmlJavaTypeAdapter(Adapter.class)
  private Map<ObserverValue, List<Link>> linkMap;

  public LinkController() {
    this.linkMap = new HashMap<>();
  }

  /**
   * Constructor for testing
   * 
   * @param asList
   */
  LinkController(List<Link> asList) {
    this();
    for (Link link : asList) {
      if (!this.linkMap.containsKey(link.getLinkType())) {
        this.linkMap.put(link.getLinkType(), new ArrayList<>());
      }
      this.linkMap.get(link.getLinkType()).add(link);
    }
  }

  /**
   * Adds a new {@link Link} to the List of {@link Link}'s mapped to the given linkType
   * 
   * @param linkType
   *          an {@link ObserverValue} for which links have been created in the
   *          {@link LinkController}
   * @param linkA
   *          the part whose {@link UUID} is the first part of the {@link Link}
   * @param linkB
   *          the part whose {@link UUID} is the second part of the {@link Link}
   * @return
   */
  public UUID addLink(ObserverValue linkType, UUID linkA, UUID linkB) {
    if (!this.linkMap.containsKey(linkType)) {
      this.linkMap.put(linkType, new ArrayList<Link>());
    }
    Link o = new Link(linkA, linkB, linkType);

    int index = this.linkMap.get(linkType).indexOf(new Link(null, linkB, linkType));
    if (index > 0 && changeLink(this.linkMap.get(linkType).get(index), linkA, linkB)) {
      return this.linkMap.get(linkType).get(index).getId();
    }
    index = this.linkMap.get(linkType).indexOf(new Link(linkA, null, linkType));
    if (index > 0 && changeLink(this.linkMap.get(linkType).get(index), linkA, linkB)) {
      return this.linkMap.get(linkType).get(index).getId();
    }
    index = this.linkMap.get(linkType).indexOf(o);
    if (index > 0) {
      return this.linkMap.get(linkType).get(index).getId();
    }
    if (this.linkMap.get(linkType).add(o)) {
      setChanged();
      notifyObservers(new UndoAddLinkingCallback(this, linkType, o));

      return o.getId();
    }
    return null;
  }

  void addLinks(List<Link> links) {
    for (Link link : links) {
      addLink(link);
    }
  }

  void addLink(Link link) {
    if (this.linkMap.containsKey(link.getLinkType())) {
      this.linkMap.get(link.getLinkType()).add(link);
    }
  }

  /**
   * this method returns a list of all UUID links stored under the given {@link ObserverValue}. If
   * <b>null</b> is given as linkType than the returned list is filled with all linked ids.
   * 
   * @param linkType
   *          an {@link ObserverValue} for which links have been created in the
   *          {@link LinkController}
   * @param part
   *          the ID of the part for which all available links are returned
   * @return a {@link List} of {@link UUID}'s of all linked items, or an empty {@link List} if part
   *         is given as <b>null</b>
   */
  public List<UUID> getLinksFor(ObserverValue linkType, UUID part) {
    List<UUID> links = new ArrayList<>();
    for (Link link : getRawLinksFor(linkType, part)) {
      links.add(link.getLinkFor(part));
    }
    return links;
  }

  /**
   * Returns all matching {@link Link} stored under the given linkType that contain the given partId
   * as link
   * 
   * @param linkType
   *          one of the LINK constants in {@link ObserverValue} <b>must not be <i>null</i></b>
   * @param partId
   *          the {@link UUID} of a {@link Link}
   * @return a {@link List} of {@link Link}'s stored under the given linkType with the given linkId
   */
  public List<Link> getRawLinksFor(ObserverValue linkType, UUID partId) {
    List<Link> links = new ArrayList<>();
    for (Link link : getLinkObjectsFor(linkType)) {
      if (link.links(partId)) {
        links.add(link);
      }
    }
    return links;
  }

  /**
   * Returns and removes all matching {@link Link} that contain the given partId as link component.
   * <p> <b>This method does not trigger an update and thus is not part of the API</b>
   * 
   * @param partId
   *          the {@link UUID} of a {@link Link}
   * @param depth
   *          the amount of recursions that are used to find {@link Link} Objects,<br> e.g. if depth
   *          is 2 than also the links are included that contain a {@link UUID} of a Link found in
   *          the first recursion
   * @return a {@link List} of {@link Link}'s that contain the given partId as link component.
   */
  List<Link> deleteLinksFor(UUID partId, int depth) {
    List<Link> links = new ArrayList<>();
    for (ObserverValue linkType : this.linkMap.keySet()) {
      List<Link> list = getRawLinksFor(linkType, partId);
      deleteLinks(linkType, list);
      links.addAll(list);
    }
    if (depth > 1) {
      List<Link> deepLinks = new ArrayList<>();
      for (Link link : links) {
        deepLinks.addAll(deleteLinksFor(link.getId(), depth - 1));
      }
      links.addAll(deepLinks);
    }
    return links;
  }

  /**
   * Changes the two link components of the {@link Link} with the given linkId.
   * 
   * @param linkType
   *          one of the LINK constants in {@link ObserverValue}
   * @param linkId
   *          the {@link UUID} of a {@link Link}
   * @param linkA
   *          the part whose {@link UUID} is the first part of the {@link Link}
   * @param linkB
   *          the part whose {@link UUID} is the second part of the {@link Link}
   * 
   * @return the first matching {@link Link} stored under the given linkType with the given linkId
   */
  public boolean changeLink(ObserverValue linkType, UUID linkId, UUID linkA, UUID linkB) {
    Link link = getLinkObjectFor(linkType, linkId);
    return changeLink(link, linkA, linkB);
  }

  /**
   * Returns the first matching {@link Link} stored under the given linkType with the given linkId
   * 
   * @param link
   *          a {@link Link}
   * @param linkA
   *          the part whose {@link UUID} is the first part of the {@link Link}
   * @param linkB
   *          the part whose {@link UUID} is the second part of the {@link Link}
   * @return the first matching {@link Link} stored under the given linkType with the given linkId
   */
  public boolean changeLink(Link link, UUID linkA, UUID linkB) {
    UUID oldA = link.getLinkA();
    UUID oldB = link.getLinkB();
    if (link.setLinkA(linkA) || link.setLinkB(linkB)) {
      setChanged();
      notifyObservers(new UndoChangeLinkingCallback(this, link.getLinkType(), link.getId(), oldA,
          oldB, linkA, linkB));
      return true;
    }

    return false;
  }

  /**
   * Returns the first matching {@link Link} stored under the given linkType with the given linkId
   * 
   * @param link
   *          the {@link Link} whose note should be changed
   * @param note
   *          a String containing the new notes for the link
   * @return the first matching {@link Link} stored under the given linkType with the given linkId
   */
  public boolean changeLinkNote(Link link, String note) {
    String oldNote = link.getNote();
    boolean isSet = link.setNote(note);
    if (isSet) {
      UndoTextChange textChange = new UndoTextChange(oldNote, note, link.getLinkType());
      textChange.setConsumer((text) -> changeLinkNote(link, text));
      setChanged();
      notifyObservers(textChange);
    }
    return isSet;
  }

  /**
   * Returns the first matching {@link Link} stored under the given linkType with the given linkId
   * 
   * @param linkType
   *          one of the LINK constants in {@link ObserverValue}
   * @param linkId
   *          the {@link UUID} of a {@link Link}
   * @return the first matching {@link Link} stored under the given linkType with the given linkId
   */
  public Link getLinkObjectFor(ObserverValue linkType, UUID linkId) {
    if (this.linkMap.containsKey(linkType)) {
      this.linkMap.get(linkType).removeIf((t) -> {
        return t.getLinkA() == null && t.getLinkB() == null;
      });
      return this.linkMap.get(linkType).stream().filter((link) -> link.getId().equals(linkId))
          .findFirst().orElse(null);
    }
    return null;
  }

  /**
   * Returns all {@link Link}'s stored under the given linkType
   * 
   * @param linkType
   *          one of the LINK constants in {@link ObserverValue}
   * @return a {@link List} containing all {@link Link}'s for the given linkType
   */
  public List<Link> getLinksFor(ObserverValue linkType) {
    return getLinkObjectsFor(linkType);
  }

  private List<Link> getLinkObjectsFor(ObserverValue linkType) {
    if (this.linkMap.containsKey(linkType)) {
      this.linkMap.get(linkType).removeIf((t) -> {
        return t.getLinkA() == null || t.getLinkB() == null;
      });
      return this.linkMap.get(linkType);
    }
    return new ArrayList<>();
  }

  /**
   * 
   * @param linkType
   *          the {@link ObserverValue} of the link
   * @param part
   *          the id of the element
   * @return whether the {@link LinkController} contains a link for the given id or not
   */
  public boolean isLinked(ObserverValue linkType, UUID part) {
    if (!isLinked(linkType, part, Optional.empty())) {
      //if the part itself is not part of a link stored under that type than maybe it is part of a link that itself is linked
      Optional<Link> optional = this.linkMap.getOrDefault(linkType, new ArrayList<>()).parallelStream().filter((link)->  {
        return link.links(part) && isLinked(linkType, link.getId());
      }).findFirst();
      return optional.isPresent();
    }
    return true;
  }

  public boolean isLinked(ObserverValue linkType, UUID part, Optional<UUID> rightPart) {
    if (this.linkMap.containsKey(linkType)) {
      for (Link link : this.linkMap.get(linkType)) {
        if (link.links(part) && (!rightPart.isPresent() || link.links(rightPart.get()))) {
          return true;
        }
      }
      if (this.linkMap.get(linkType).isEmpty()) {
        this.linkMap.remove(linkType);
      }
    }
    return false;

  }

  /**
   * Finds and removes the first matching {@link Link} stored under the given linkType with the
   * given linkId
   * 
   * @param linkType
   *          one of the LINK constants in {@link ObserverValue}
   * @param linkId
   *          the {@link UUID} of a {@link Link}
   * @return whether something has been deleted or not
   */
  public boolean deleteLink(ObserverValue linkType, UUID linkId) {
    if (this.linkMap.containsKey(linkType)) {
      return this.linkMap.get(linkType).removeIf((t) -> {
        return t.getId().equals(linkId);
      });
    }
    return false;
  }

  /**
   * Finds and deletes a {@link Link} based on the two parts of the link
   * 
   * @param linkType
   *          one of the LINK constants in {@link ObserverValue}
   * @param linkA
   *          the part whose {@link UUID} is the first part of the {@link Link}
   * @param linkB
   *          the part whose {@link UUID} is the second part of the {@link Link}
   * @return
   */
  public boolean deleteLink(ObserverValue linkType, UUID linkA, UUID linkB) {
    if (this.linkMap.containsKey(linkType)) {
      Link o = new Link(linkA, linkB, linkType);
      if (this.linkMap.get(linkType).remove(o)) {
        setChanged();
        notifyObservers(new UndoRemoveLinkingCallback(this, linkType, o));
        return true;
      }
    }
    return false;
  }

  /**
   * 
   * @param linkType
   *          the {@link ObserverValue} of the link
   * @param part
   *          the part that should be included in all links that are to to be deleted,<br> or
   *          <b><i>null</i></b> if all links for the given <b>type</b> should be deleted
   */
  public void deleteAllFor(ObserverValue linkType, UUID part) {
    List<Link> links = new ArrayList<>();
    if (this.linkMap.containsKey(linkType)) {
      for (Link link : this.linkMap.get(linkType)) {
        if (part == null || link.links(part)) {
          links.add(link);
        }
      }
      deleteLinks(linkType, links);
      setChanged();
      notifyObservers(new UndoRemoveLinkingCallback(this, linkType, links));
    }
  }

  void deleteLinks(ObserverValue linkType, List<Link> links) {
    if (this.linkMap.containsKey(linkType)) {
      this.linkMap.get(linkType).removeAll(links);
      if (this.linkMap.get(linkType).isEmpty()) {
        this.linkMap.remove(linkType);
      }
    }
  }

  public int getLinkMapSize() {
    return this.linkMap.size();
  }
}
