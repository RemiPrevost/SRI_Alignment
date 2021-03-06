/*
 * $Id: BasicCell.java 1995 2014-11-19 12:39:39Z euzenat $
 *
 * Copyright (C) INRIA, 2003-2005, 2007-2010
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA.
 */

package fr.inrialpes.exmo.align.impl;

import java.net.URI;
import java.util.Collection;

import org.xml.sax.ContentHandler;

import org.semanticweb.owl.align.Alignment;
import org.semanticweb.owl.align.AlignmentException;
import org.semanticweb.owl.align.AlignmentVisitor;
import org.semanticweb.owl.align.Cell;
import org.semanticweb.owl.align.Relation;

/**
 * Represents an ontology alignment correspondence.
 *
 * @author J�r�me Euzenat
 * @version $Id: BasicCell.java 1995 2014-11-19 12:39:39Z euzenat $
 */

public class BasicCell implements Cell, Comparable<Cell>, Extensible {
    public void accept( AlignmentVisitor visitor) throws AlignmentException {
        visitor.visit( this );
    }

    protected String id = null;
    protected String semantics = null;
    protected Object object1 = null;
    protected Object object2 = null;
    protected Relation relation = null;
    protected double strength = 0;
    protected Extensions extensions = null;

    /** Creation **/
    public BasicCell( String id, Object ob1, Object ob2, Relation rel, double m ) throws AlignmentException {
	if ( ob1 == null ) throw new AlignmentException( "object1 cannot be null" );
	if ( ob2 == null ) throw new AlignmentException( "object2 cannot be null" );
	setId( id );
	object1 = ob1;
	object2 = ob2;
	relation = rel;
	// No exception, just keep 0?
	if ( m >= 0 && m <= 1 ) strength = m;
	// extensions is only created on demand, otherwise, it is too expensive
    };

    // the strength must be compared with regard to abstract types
    public boolean equals( Object c ) {
	if ( c != null && c instanceof Cell ) return equals( (Cell)c );
	else return false;
    }

    public boolean equals ( Cell c ) {
	if ( c == null ) return false;
	else return ( object1.equals(((BasicCell)c).getObject1()) && object2.equals(((BasicCell)c).getObject2()) && strength == ((BasicCell)c).getStrength() && (relation.equals( ((BasicCell)c).getRelation() )) );
    }

    public int hashCode() {
	return 17 + 7*object1.hashCode() + 11*object2.hashCode() + relation.hashCode() + (int)(strength*150.);
    }

    /**
     * Used to order the cells in an alignment:
     * -- this > c iff this.getStrength() < c.getStrength() --
     */
    public int compareTo( Cell c ){
	if ( this == c ) return 0; // compatible with equals!
	//if ( ! (c instanceof Cell) ) return 1;
	if ( c.getStrength() > getStrength() ) return 1;
	if ( getStrength() > c.getStrength() ) return -1;
	/*
	  // Impossible to compare objects!
	int result = 0;
	result = object1.compareTo( ((BasicCell)c).getObject1() );
	if ( result != 0 ) return result;
	result = object2.compareTo( ((BasicCell)c).getObject2() );
	if ( result != 0 ) return result;
	result = relation.compareTo( ((BasicCell)c).getRelation() );
	return result; // compatible with equals!
	*/
	// same strength
	if ( equals( c ) ) return 0; // compatible with equals!
	// Now: if they are different, we do not want to say that they are equal
	// But the compareTo contract says that the resut should be compatible
	if ( hashCode() > c.hashCode() ) return 1;
	else return -1; // (hoping hashCode will be different in all cases)
    }

    public String getId(){ return id; };
    public void setId( String id ){ this.id = id; };
    public String getSemantics(){
	if ( semantics != null ) { return semantics; }
	else { return "first-order"; }
    };
    public void setSemantics( String sem ){ semantics = sem; };
    public Object getObject1(){ return object1; };
    public Object getObject2(){ return object2; };
    /**
     * Since version 3.3, the interpretation of objects (and thus finding their
     * URI) depends on the Ontology API which is used. This information is not
     * stored in the Cells (this would cost two pointers per cell) and thus,
     * most of the time, this will raise an exception.
     * Use <tt>Ontology.getEntityURI( this )</tt> instead.
     */
    public URI getObject1AsURI() throws AlignmentException {
	return getObject1AsURI( null );
    }
    public URI getObject1AsURI( Alignment al ) throws AlignmentException {
	if ( object1 instanceof URI ) {
	    return (URI)object1;
	} else {
	    throw new AlignmentException( "Cannot find URI for "+object1 );
	}
    }
    /**
     * Since version 3.3, the interpretation of objects (and thus finding their
     * URI) depends on the Ontology API which is used. This information is not
     * stored in the Cells (this would cost two pointers per cell) and thus,
     * most of the time, this will raise an exception.
     * Use <tt>Ontology.getEntityURI( this )</tt> instead.
     */
    public URI getObject2AsURI() throws AlignmentException {
	return getObject2AsURI( null );
    }
    public URI getObject2AsURI( Alignment al ) throws AlignmentException {
	if ( object2 instanceof URI ) {
	    return (URI)object2;
	} else {
	    throw new AlignmentException( "Cannot find URI for "+object2 );
	}
    }
    public void setObject1( Object ob ) throws AlignmentException {
	if ( ob == null ) throw new AlignmentException( "object1 cannot be null" );
	object1 = ob;
    }
    public void setObject2( Object ob ) throws AlignmentException {
	if ( ob == null ) throw new AlignmentException( "object2 cannot be null" );
	object2 = ob;
    }
    public Relation getRelation(){ return relation; };
    public void setRelation( Relation rel ){ relation = rel; };
    public double getStrength(){ return strength; };
    public void setStrength( double m ){ strength = m; };

    public Collection<String[]> getExtensions(){ 
	if ( extensions != null ) return extensions.getValues();
	else return null;
    }
    public void setExtensions( Extensions p ){
	extensions = p;
    }

    public void setExtension( String uri, String label, String value ) {
	if ( extensions == null ) extensions = new Extensions();
	extensions.setExtension( uri, label, value );
    };

    public String getExtension( String uri, String label ) {
	if ( extensions != null ) {
	    return extensions.getExtension( uri, label );
	} else {
	    return (String)null;
	}
    };

    public Cell inverse() throws AlignmentException {
	BasicCell result = new BasicCell( (String)null, object2, object1, relation.inverse(), strength );
	if ( extensions != null ) {
	    Extensions newext = (Extensions)extensions.clone();
	    newext.unsetExtension( Namespace.ALIGNMENT.getUriPrefix(), Annotations.ID );
	    result.setExtensions( newext );
	}
	// The sae should be done for the measure
	return result;
    }

    public Cell compose(Cell c) throws AlignmentException {
    	if (!object2.equals(c.getObject1()) && relation.compose(c.getRelation())==null )
    		return null;
    	Cell result = (Cell)new BasicCell( (String)null, object1, c.getObject2(), relation.compose(c.getRelation()), strength*c.getStrength() );
    	// TODO : extension...
    	return result;
    }

    /** Housekeeping **/
    public void dump( ContentHandler h ){};

}

