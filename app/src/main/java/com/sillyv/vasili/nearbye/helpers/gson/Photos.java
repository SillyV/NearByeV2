package com.sillyv.vasili.nearbye.helpers.gson;

import java.util.List;

/**
 * Created by Vasili.Fedotov on 5/27/2016.
 */
public class Photos
{
    private int height;
    private int width;
    private List<String> html_attributions;
    private String photo_reference;

    public Photos(int height, int width, List<String> html_attributions, String photo_reference)
    {

        this.height = height;
        this.width = width;
        this.html_attributions = html_attributions;
        this.photo_reference = photo_reference;
    }

    public int getHeight()
    {
        return height;
    }

    public void setHeight(int height)
    {
        this.height = height;
    }

    public int getWidth()
    {
        return width;
    }

    public void setWidth(int width)
    {
        this.width = width;
    }

    public List<String> getHtml_attributions()
    {
        return html_attributions;
    }

    public void setHtml_attributions(List<String> html_attributions)
    {
        this.html_attributions = html_attributions;
    }

    public String getPhoto_reference()
    {
        return photo_reference;
    }

    public void setPhoto_reference(String photo_reference)
    {
        this.photo_reference = photo_reference;
    }
}
