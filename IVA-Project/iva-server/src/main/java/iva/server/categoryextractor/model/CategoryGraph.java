/**
 * 
 */
package iva.server.categoryextractor.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jgrapht.EdgeFactory;
import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

/**
 * Wrapper class for {@link org.jgrapht.UndirectedGraph} and functions from 
 * {@link org.jgrapht.Graphs} intended for creating a semantic net of DBpedia 
 * categories.
 * @author Aron
 */
public class CategoryGraph implements Serializable {
	private static final long serialVersionUID = -2833446020266857800L;
	
	private final UndirectedGraph<String, DefaultEdge> graph = new SimpleGraph<>(DefaultEdge.class);
	private Set<String> expandedVertices = new HashSet<>();
	
	public CategoryGraph() {
	}
	
	public CategoryGraph(CategoryGraph graph) {
		addGraph(graph);
	}

	/**
	 * @param sourceVertex
	 * @param targetVertex
	 * @return
	 * @see org.jgrapht.Graph#addEdge(java.lang.Object, java.lang.Object)
	 */
	public DefaultEdge addEdge(String sourceVertex, String targetVertex) {
		return graph.addEdge(sourceVertex, targetVertex);
	}

	/**
	 * @param v
	 * @return
	 * @see org.jgrapht.Graph#addVertex(java.lang.Object)
	 */
	public boolean addVertex(String v) {
		return graph.addVertex(v);
	}

	/**
	 * @param e
	 * @return
	 * @see org.jgrapht.Graph#containsEdge(java.lang.Object)
	 */
	public boolean containsEdge(DefaultEdge e) {
		return graph.containsEdge(e);
	}

	/**
	 * @param sourceVertex
	 * @param targetVertex
	 * @return
	 * @see org.jgrapht.Graph#containsEdge(java.lang.Object, java.lang.Object)
	 */
	public boolean containsEdge(String sourceVertex, String targetVertex) {
		return graph.containsEdge(sourceVertex, targetVertex);
	}

	/**
	 * @param v
	 * @return
	 * @see org.jgrapht.Graph#containsVertex(java.lang.Object)
	 */
	public boolean containsVertex(String v) {
		return graph.containsVertex(v);
	}

	/**
	 * @param vertex
	 * @return
	 * @see org.jgrapht.UndirectedGraph#degreeOf(java.lang.Object)
	 */
	public int degreeOf(String vertex) {
		return graph.degreeOf(vertex);
	}

	/**
	 * @return
	 * @see org.jgrapht.Graph#edgeSet()
	 */
	public Set<DefaultEdge> edgeSet() {
		return graph.edgeSet();
	}

	/**
	 * @param vertex
	 * @return
	 * @see org.jgrapht.Graph#edgesOf(java.lang.Object)
	 */
	public Set<DefaultEdge> edgesOf(String vertex) {
		return graph.edgesOf(vertex);
	}

	/**
	 * @param sourceVertex
	 * @param targetVertex
	 * @return
	 * @see org.jgrapht.Graph#getAllEdges(java.lang.Object, java.lang.Object)
	 */
	public Set<DefaultEdge> getAllEdges(String sourceVertex, String targetVertex) {
		return graph.getAllEdges(sourceVertex, targetVertex);
	}

	/**
	 * @param sourceVertex
	 * @param targetVertex
	 * @return
	 * @see org.jgrapht.Graph#getEdge(java.lang.Object, java.lang.Object)
	 */
	public DefaultEdge getEdge(String sourceVertex, String targetVertex) {
		return graph.getEdge(sourceVertex, targetVertex);
	}

	/**
	 * @return
	 * @see org.jgrapht.Graph#getEdgeFactory()
	 */
	public EdgeFactory<String, DefaultEdge> getEdgeFactory() {
		return graph.getEdgeFactory();
	}

	/**
	 * @param e
	 * @return
	 * @see org.jgrapht.Graph#getEdgeSource(java.lang.Object)
	 */
	public String getEdgeSource(DefaultEdge e) {
		return graph.getEdgeSource(e);
	}

	/**
	 * @param e
	 * @return
	 * @see org.jgrapht.Graph#getEdgeTarget(java.lang.Object)
	 */
	public String getEdgeTarget(DefaultEdge e) {
		return graph.getEdgeTarget(e);
	}

	/**
	 * @param edges
	 * @return
	 * @see org.jgrapht.Graph#removeAllEdges(java.util.Collection)
	 */
	public boolean removeAllEdges(Collection<? extends DefaultEdge> edges) {
		return graph.removeAllEdges(edges);
	}

	/**
	 * @param sourceVertex
	 * @param targetVertex
	 * @return
	 * @see org.jgrapht.Graph#removeAllEdges(java.lang.Object, java.lang.Object)
	 */
	public Set<DefaultEdge> removeAllEdges(String sourceVertex, String targetVertex) {
		return graph.removeAllEdges(sourceVertex, targetVertex);
	}

	/**
	 * @param vertices
	 * @return
	 * @see org.jgrapht.Graph#removeAllVertices(java.util.Collection)
	 */
	public boolean removeAllVertices(Collection<? extends String> vertices) {
		return graph.removeAllVertices(vertices);
	}

	/**
	 * @param e
	 * @return
	 * @see org.jgrapht.Graph#removeEdge(java.lang.Object)
	 */
	public boolean removeEdge(DefaultEdge e) {
		return graph.removeEdge(e);
	}

	/**
	 * @param sourceVertex
	 * @param targetVertex
	 * @return
	 * @see org.jgrapht.Graph#removeEdge(java.lang.Object, java.lang.Object)
	 */
	public DefaultEdge removeEdge(String sourceVertex, String targetVertex) {
		return graph.removeEdge(sourceVertex, targetVertex);
	}

	/**
	 * @param v
	 * @return
	 * @see org.jgrapht.Graph#removeVertex(java.lang.Object)
	 */
	public boolean removeVertex(String v) {
		return graph.removeVertex(v);
	}

	/**
	 * @return
	 * @see org.jgrapht.Graph#vertexSet()
	 */
	public Set<String> vertexSet() {
		return graph.vertexSet();
	}

	/**
	 * @param vertices
	 * @return
	 * @see org.jgrapht.Graphs#addAllVertices(org.jgrapht.Graph, Collection)
	 */
	public boolean addAllVertices(Collection<? extends String> vertices) {
		return Graphs.addAllVertices(graph, vertices);
	}
	
	/**
	 * @param sourceVertex
	 * @param targetVertex
	 * @return
	 * @see org.jgrapht.Graphs#addEdgeWithVertices(Graph, Object, Object)
	 */
	public DefaultEdge addEdgeWithVertices(String sourceVertex, String targetVertex) {
		return Graphs.addEdgeWithVertices(graph, sourceVertex, targetVertex);
	}
	
	/**
	 * @param vertex
	 * @return
	 * @see org.jgrapht.Graphs#neighborListOf(org.jgrapht.Graph, Object)
	 */
	public List<String> getNeighbourListOf(String vertex) {
		return Graphs.neighborListOf(graph, vertex);
	}
	
	/**
	 * @param graph
	 * @return
	 * @see org.jgrapht.Graphs#addGraph(Graph, Graph)
	 */
	public boolean addGraph(CategoryGraph graph) {
		return Graphs.addGraph(this.graph, graph.graph);
	}

	public Set<String> getExpandedVertices() {
		return expandedVertices;
	}

	public void setExpandedVertices(Set<String> expandedVertices) {
		this.expandedVertices = expandedVertices;
	}
	
}
