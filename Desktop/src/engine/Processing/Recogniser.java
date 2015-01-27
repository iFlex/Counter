// Process the data to put into the counter
package engine.Processing;
import engine.util.Data;
public interface Recogniser
{
	public abstract void  setModel(String path);
	public abstract void process(Data data);
}
