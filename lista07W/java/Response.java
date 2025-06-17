import java.io.Serializable;

public class Response implements Serializable {
  public enum Status {
    SUCCESS, FAILURE
  }

  public Status status;
  public String tree;

  public Response(Status status) {
    this.status = status;
  }

  public Response(Status status, String tree) {
    this.status = status;
    this.tree = tree;
  }
}