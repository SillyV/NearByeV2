package com.sillyv.vasili.nearbye.helpers.gson;

import java.util.List;

/**
 * Created by Vasili.Fedotov on 5/27/2016.
 */

public class GooglePlacesHolder
{
    private List<String> html_attributions;
    private String next_page_token;
    private List<Results> results;
    private String status;
    //private List<String> debug_info;



    public GooglePlacesHolder(List<String> html_attributions, String next_page_token, List<Results> results, String status)
    {

//        this.debug_info = debug_info;
        this.html_attributions = html_attributions;
        this.next_page_token = next_page_token;
        this.results = results;
        this.status = status;
    }

    public List<String> getHtml_attributions()
    {
        return html_attributions;
    }

    public void setHtml_attributions(List<String> html_attributions)
    {
        this.html_attributions = html_attributions;
    }

    public String getNext_page_token()
    {
        return next_page_token;
    }

    public void setNext_page_token(String next_page_token)
    {
        this.next_page_token = next_page_token;
    }

    public List<Results> getResults()
    {
        return results;
    }

    public void setResults(List<Results> results)
    {
        this.results = results;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

//    public List<String> getDebug_info()
//    {
//        return debug_info;
//    }
//
//    public void setDebug_info(List<String> debug_info)
//    {
//        this.debug_info = debug_info;
//    }
}
