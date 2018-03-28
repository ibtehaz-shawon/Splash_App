package ninja.ibtehaz.splash.application;

/**
 * Created by ibteh on 2/20/2017.
 */

public class ApiConfig {

    private String BASE_URL = "https://python-splash.herokuapp.com/unsplash/";


    /**
     * https://python-splash.herokuapp.com/unsplash/feed?page=10
     * @param pageNumber
     * @return
     */
    public String GET_FEED_URL(int pageNumber) {
        return BASE_URL + "feed?page="+pageNumber;
    }
}

