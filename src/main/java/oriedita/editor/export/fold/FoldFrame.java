package oriedita.editor.export.fold;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Optional;

public class FoldFrame {
    private String frameAuthor;
    private String frameTitle;
    private String frameDescription;
    private String[] frameClasses;
    private String[] frameAttributes;
    private String frameUnit;
    private Optional<Integer> frameParent;
    private Optional<Boolean> frameInherit;
    private double[][] verticesCoords;
    private int[] verticesVertices;
    private int[][] verticesFaces;
    private int[][] edgesVertices;
    private FoldEdgeAssignment[] edgesAssignment;
    private double[] edgesFoldAngle;
    private double[] edgesLength;
    private int[][] facesVertices;
    private int[][] facesEdges;
    private int[][] faceOrders;
    private int[][] edgeOrders;

    @JsonProperty("frame_author")
    public String getFrameAuthor() {
        return frameAuthor;
    }

    @JsonProperty("frame_author")
    public void setFrameAuthor(String frameAuthor) {
        this.frameAuthor = frameAuthor;
    }

    @JsonProperty("frame_title")
    public String getFrameTitle() {
        return frameTitle;
    }

    @JsonProperty("frame_title")
    public void setFrameTitle(String frameTitle) {
        this.frameTitle = frameTitle;
    }

    @JsonProperty("frame_description")
    public String getFrameDescription() {
        return frameDescription;
    }

    @JsonProperty("frame_description")
    public void setFrameDescription(String frameDescription) {
        this.frameDescription = frameDescription;
    }

    @JsonProperty("frame_classes")
    public String[] getFrameClasses() {
        return frameClasses;
    }

    @JsonProperty("frame_classes")
    public void setFrameClasses(String[] frameClasses) {
        this.frameClasses = frameClasses;
    }

    @JsonProperty("frame_attributes")
    public String[] getFrameAttributes() {
        return frameAttributes;
    }

    @JsonProperty("frame_attributes")
    public void setFrameAttributes(String[] frameAttributes) {
        this.frameAttributes = frameAttributes;
    }

    @JsonProperty("frame_unit")
    public String getFrameUnit() {
        return frameUnit;
    }

    @JsonProperty("frame_unit")
    public void setFrameUnit(String frameUnit) {
        this.frameUnit = frameUnit;
    }

    @JsonProperty("frame_parent")
    public Optional<Integer> getFrameParent() {
        return frameParent;
    }

    @JsonProperty("frame_parent")
    public void setFrameParent(Optional<Integer> frameParent) {
        this.frameParent = frameParent;
    }

    @JsonProperty("frame_inherit")
    public Optional<Boolean> isFrameInherit() {
        return frameInherit;
    }

    @JsonProperty("frame_inherit")
    public void setFrameInherit(Optional<Boolean> frameInherit) {
        this.frameInherit = frameInherit;
    }

    @JsonProperty("vertices_coords")
    public double[][] getVerticesCoords() {
        return verticesCoords;
    }

    @JsonProperty("vertices_coords")
    public void setVerticesCoords(double[][] verticesCoords) {
        this.verticesCoords = verticesCoords;
    }

    @JsonProperty("vertices_vertices")
    public int[] getVerticesVertices() {
        return verticesVertices;
    }

    @JsonProperty("vertices_vertices")
    public void setVerticesVertices(int[] verticesVertices) {
        this.verticesVertices = verticesVertices;
    }

    @JsonProperty("vertices_faces")
    public int[][] getVerticesFaces() {
        return verticesFaces;
    }

    @JsonProperty("vertices_faces")
    public void setVerticesFaces(int[][] verticesFaces) {
        this.verticesFaces = verticesFaces;
    }

    @JsonProperty("edges_vertices")
    public int[][] getEdgesVertices() {
        return edgesVertices;
    }

    @JsonProperty("edges_vertices")
    public void setEdgesVertices(int[][] edgesVertices) {
        this.edgesVertices = edgesVertices;
    }

    @JsonProperty("edges_assignment")
    public FoldEdgeAssignment[] getEdgesAssignment() {
        return edgesAssignment;
    }

    @JsonProperty("edges_assignment")
    public void setEdgesAssignment(FoldEdgeAssignment[] edgesAssignment) {
        this.edgesAssignment = edgesAssignment;
    }

    @JsonProperty("edges_foldAngle")
    public double[] getEdgesFoldAngle() {
        return edgesFoldAngle;
    }

    @JsonProperty("edges_foldAngle")
    public void setEdgesFoldAngle(double[] edgesFoldAngle) {
        this.edgesFoldAngle = edgesFoldAngle;
    }

    @JsonProperty("edges_length")
    public double[] getEdgesLength() {
        return edgesLength;
    }

    @JsonProperty("edges_length")
    public void setEdgesLength(double[] edgesLength) {
        this.edgesLength = edgesLength;
    }

    @JsonProperty("faces_vertices")
    public int[][] getFacesVertices() {
        return facesVertices;
    }

    @JsonProperty("faces_vertices")
    public void setFacesVertices(int[][] facesVertices) {
        this.facesVertices = facesVertices;
    }

    @JsonProperty("faces_edges")
    public int[][] getFacesEdges() {
        return facesEdges;
    }

    @JsonProperty("faces_edges")
    public void setFacesEdges(int[][] facesEdges) {
        this.facesEdges = facesEdges;
    }

    @JsonProperty("faces_orders")
    public int[][] getFaceOrders() {
        return faceOrders;
    }

    @JsonProperty("faces_orders")
    public void setFaceOrders(int[][] faceOrders) {
        this.faceOrders = faceOrders;
    }

    @JsonProperty("edge_orders")
    public int[][] getEdgeOrders() {
        return edgeOrders;
    }

    @JsonProperty("edge_orders")
    public void setEdgeOrders(int[][] edgeOrders) {
        this.edgeOrders = edgeOrders;
    }
}
