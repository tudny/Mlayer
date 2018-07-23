package mlayer.app.classes;

import java.util.Random;

public class RandomRange extends Random {

    /*public RandomRange(){
        super();
    }*/

    public Integer nextInt(int begin, int end) throws Exception{
        if(begin > end) throw new Exception("Beginning can't be bigger then Ending");
        return begin + nextInt(end - begin + 1);
    }
}
