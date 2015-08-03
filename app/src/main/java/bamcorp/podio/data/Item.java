
package bamcorp.podio.data;


/**
* Data class for list item
* */
public class Item
{
    ItemType type;
    String name;

    public Item(ItemType type, String name)
    {
        this.type = type;
        this.name = name;
    }

    public ItemType getType()
    {
        return type;
    }

    public String getName()
    {
        return name;
    }

}
