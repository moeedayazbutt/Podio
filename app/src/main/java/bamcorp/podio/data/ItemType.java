
package bamcorp.podio.data;

/**
* An item type:
* 1. Header
* 2. Item
* */
public enum ItemType
{
    HEADER(0),
    ITEM(1);

    private int value;

    ItemType(int value)
    {
        this.value = value;
    }

    public int getValue()
    {
        return value;
    }

    public static ItemType getEnum(int value)
    {
        ItemType[] states = ItemType.values();
        for (ItemType state : states)
        {
            if (state.getValue() == value)
                return state;
        }

        return null;
    }
}
