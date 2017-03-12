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


    /**
     * https://python-splash.herokuapp.com/unsplash/get_collection?method=2
     * @param pageNumber
     * @param method
     * @return
     */
    public String GET_COLLECTION_URL(int pageNumber, int method) {
        return BASE_URL + "get_collection?method="+method+"&page="+pageNumber;
    }
}

