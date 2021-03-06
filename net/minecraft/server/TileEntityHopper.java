package net.minecraft.server;

import java.util.List;

public class TileEntityHopper extends TileEntity implements IHopper {

    private ItemStack[] a = new ItemStack[5];
    private String i;
    private int j = -1;

    public TileEntityHopper() {}

    public void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        NBTTagList nbttaglist = nbttagcompound.getList("Items", 10);

        this.a = new ItemStack[this.getSize()];
        if (nbttagcompound.hasKeyOfType("CustomName", 8)) {
            this.i = nbttagcompound.getString("CustomName");
        }

        this.j = nbttagcompound.getInt("TransferCooldown");

        for (int i = 0; i < nbttaglist.size(); ++i) {
            NBTTagCompound nbttagcompound1 = nbttaglist.get(i);
            byte b0 = nbttagcompound1.getByte("Slot");

            if (b0 >= 0 && b0 < this.a.length) {
                this.a[b0] = ItemStack.createStack(nbttagcompound1);
            }
        }
    }

    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        NBTTagList nbttaglist = new NBTTagList();

        for (int i = 0; i < this.a.length; ++i) {
            if (this.a[i] != null) {
                NBTTagCompound nbttagcompound1 = new NBTTagCompound();

                nbttagcompound1.setByte("Slot", (byte) i);
                this.a[i].save(nbttagcompound1);
                nbttaglist.add(nbttagcompound1);
            }
        }

        nbttagcompound.set("Items", nbttaglist);
        nbttagcompound.setInt("TransferCooldown", this.j);
        if (this.k_()) {
            nbttagcompound.setString("CustomName", this.i);
        }
    }

    public void update() {
        super.update();
    }

    public int getSize() {
        return this.a.length;
    }

    public ItemStack getItem(int i) {
        return this.a[i];
    }

    public ItemStack splitStack(int i, int j) {
        if (this.a[i] != null) {
            ItemStack itemstack;

            if (this.a[i].count <= j) {
                itemstack = this.a[i];
                this.a[i] = null;
                return itemstack;
            } else {
                itemstack = this.a[i].a(j);
                if (this.a[i].count == 0) {
                    this.a[i] = null;
                }

                return itemstack;
            }
        } else {
            return null;
        }
    }

    public ItemStack splitWithoutUpdate(int i) {
        if (this.a[i] != null) {
            ItemStack itemstack = this.a[i];

            this.a[i] = null;
            return itemstack;
        } else {
            return null;
        }
    }

    public void setItem(int i, ItemStack itemstack) {
        this.a[i] = itemstack;
        if (itemstack != null && itemstack.count > this.getMaxStackSize()) {
            itemstack.count = this.getMaxStackSize();
        }
    }

    public String getInventoryName() {
        return this.k_() ? this.i : "container.hopper";
    }

    public boolean k_() {
        return this.i != null && this.i.length() > 0;
    }

    public void a(String s) {
        this.i = s;
    }

    public int getMaxStackSize() {
        return 64;
    }

    public boolean a(EntityHuman entityhuman) {
        return this.world.getTileEntity(this.x, this.y, this.z) != this ? false : entityhuman.e((double) this.x + 0.5D, (double) this.y + 0.5D, (double) this.z + 0.5D) <= 64.0D;
    }

    public void startOpen() {}

    public void closeContainer() {}

    public boolean b(int i, ItemStack itemstack) {
        return true;
    }

    public void h() {
        if (this.world != null && !this.world.isStatic) {
            --this.j;
            if (!this.j()) {
                this.c(0);
                this.i();
            }
        }
    }

    public boolean i() {
        if (this.world != null && !this.world.isStatic) {
            if (!this.j() && BlockHopper.c(this.p())) {
                boolean flag = false;

                if (!this.k()) {
                    flag = this.y();
                }

                if (!this.l()) {
                    flag = suckInItems(this) || flag;
                }

                if (flag) {
                    this.c(8);
                    this.update();
                    return true;
                }
            }

            return false;
        } else {
            return false;
        }
    }

    private boolean k() {
        ItemStack[] aitemstack = this.a;
        int i = aitemstack.length;

        for (int j = 0; j < i; ++j) {
            ItemStack itemstack = aitemstack[j];

            if (itemstack != null) {
                return false;
            }
        }

        return true;
    }

    private boolean l() {
        ItemStack[] aitemstack = this.a;
        int i = aitemstack.length;

        for (int j = 0; j < i; ++j) {
            ItemStack itemstack = aitemstack[j];

            if (itemstack == null || itemstack.count != itemstack.getMaxStackSize()) {
                return false;
            }
        }

        return true;
    }

    private boolean y() {
        IInventory iinventory = this.z();

        if (iinventory == null) {
            return false;
        } else {
            int i = Facing.OPPOSITE_FACING[BlockHopper.b(this.p())];

            if (this.a(iinventory, i)) {
                return false;
            } else {
                for (int j = 0; j < this.getSize(); ++j) {
                    if (this.getItem(j) != null) {
                        ItemStack itemstack = this.getItem(j).cloneItemStack();
                        ItemStack itemstack1 = addItem(iinventory, this.splitStack(j, 1), i);

                        if (itemstack1 == null || itemstack1.count == 0) {
                            iinventory.update();
                            return true;
                        }

                        this.setItem(j, itemstack);
                    }
                }

                return false;
            }
        }
    }

    private boolean a(IInventory iinventory, int i) {
        if (iinventory instanceof IWorldInventory && i > -1) {
            IWorldInventory iworldinventory = (IWorldInventory) iinventory;
            int[] aint = iworldinventory.getSlotsForFace(i);

            for (int j = 0; j < aint.length; ++j) {
                ItemStack itemstack = iworldinventory.getItem(aint[j]);

                if (itemstack == null || itemstack.count != itemstack.getMaxStackSize()) {
                    return false;
                }
            }
        } else {
            int k = iinventory.getSize();

            for (int l = 0; l < k; ++l) {
                ItemStack itemstack1 = iinventory.getItem(l);

                if (itemstack1 == null || itemstack1.count != itemstack1.getMaxStackSize()) {
                    return false;
                }
            }
        }

        return true;
    }

    private static boolean b(IInventory iinventory, int i) {
        if (iinventory instanceof IWorldInventory && i > -1) {
            IWorldInventory iworldinventory = (IWorldInventory) iinventory;
            int[] aint = iworldinventory.getSlotsForFace(i);

            for (int j = 0; j < aint.length; ++j) {
                if (iworldinventory.getItem(aint[j]) != null) {
                    return false;
                }
            }
        } else {
            int k = iinventory.getSize();

            for (int l = 0; l < k; ++l) {
                if (iinventory.getItem(l) != null) {
                    return false;
                }
            }
        }

        return true;
    }

    public static boolean suckInItems(IHopper ihopper) {
        IInventory iinventory = getSourceInventory(ihopper);

        if (iinventory != null) {
            byte b0 = 0;

            if (b(iinventory, b0)) {
                return false;
            }

            if (iinventory instanceof IWorldInventory && b0 > -1) {
                IWorldInventory iworldinventory = (IWorldInventory) iinventory;
                int[] aint = iworldinventory.getSlotsForFace(b0);

                for (int i = 0; i < aint.length; ++i) {
                    if (tryTakeInItemFromSlot(ihopper, iinventory, aint[i], b0)) {
                        return true;
                    }
                }
            } else {
                int j = iinventory.getSize();

                for (int k = 0; k < j; ++k) {
                    if (tryTakeInItemFromSlot(ihopper, iinventory, k, b0)) {
                        return true;
                    }
                }
            }
        } else {
            EntityItem entityitem = getEntityItemAt(ihopper.getWorld(), ihopper.x(), ihopper.aD() + 1.0D, ihopper.aE());

            if (entityitem != null) {
                return addEntityItem(ihopper, entityitem);
            }
        }

        return false;
    }

    private static boolean tryTakeInItemFromSlot(IHopper ihopper, IInventory iinventory, int i, int j) {
        ItemStack itemstack = iinventory.getItem(i);

        if (itemstack != null && canTakeItemFromInventory(iinventory, itemstack, i, j)) {
            ItemStack itemstack1 = itemstack.cloneItemStack();
            ItemStack itemstack2 = addItem(ihopper, iinventory.splitStack(i, 1), -1);

            if (itemstack2 == null || itemstack2.count == 0) {
                iinventory.update();
                return true;
            }

            iinventory.setItem(i, itemstack1);
        }

        return false;
    }

    public static boolean addEntityItem(IInventory iinventory, EntityItem entityitem) {
        boolean flag = false;

        if (entityitem == null) {
            return false;
        } else {
            ItemStack itemstack = entityitem.getItemStack().cloneItemStack();
            ItemStack itemstack1 = addItem(iinventory, itemstack, -1);

            if (itemstack1 != null && itemstack1.count != 0) {
                entityitem.setItemStack(itemstack1);
            } else {
                flag = true;
                entityitem.die();
            }

            return flag;
        }
    }

    public static ItemStack addItem(IInventory iinventory, ItemStack itemstack, int i) {
        if (iinventory instanceof IWorldInventory && i > -1) {
            IWorldInventory iworldinventory = (IWorldInventory) iinventory;
            int[] aint = iworldinventory.getSlotsForFace(i);

            for (int j = 0; j < aint.length && itemstack != null && itemstack.count > 0; ++j) {
                itemstack = tryMoveInItem(iinventory, itemstack, aint[j], i);
            }
        } else {
            int k = iinventory.getSize();

            for (int l = 0; l < k && itemstack != null && itemstack.count > 0; ++l) {
                itemstack = tryMoveInItem(iinventory, itemstack, l, i);
            }
        }

        if (itemstack != null && itemstack.count == 0) {
            itemstack = null;
        }

        return itemstack;
    }

    private static boolean canPlaceItemInInventory(IInventory iinventory, ItemStack itemstack, int i, int j) {
        return !iinventory.b(i, itemstack) ? false : !(iinventory instanceof IWorldInventory) || ((IWorldInventory) iinventory).canPlaceItemThroughFace(i, itemstack, j);
    }

    private static boolean canTakeItemFromInventory(IInventory iinventory, ItemStack itemstack, int i, int j) {
        return !(iinventory instanceof IWorldInventory) || ((IWorldInventory) iinventory).canTakeItemThroughFace(i, itemstack, j);
    }

    private static ItemStack tryMoveInItem(IInventory iinventory, ItemStack itemstack, int i, int j) {
        ItemStack itemstack1 = iinventory.getItem(i);

        if (canPlaceItemInInventory(iinventory, itemstack, i, j)) {
            boolean flag = false;

            if (itemstack1 == null) {
                iinventory.setItem(i, itemstack);
                itemstack = null;
                flag = true;
            } else if (canMergeItems(itemstack1, itemstack)) {
                int k = itemstack.getMaxStackSize() - itemstack1.count;
                int l = Math.min(itemstack.count, k);

                itemstack.count -= l;
                itemstack1.count += l;
                flag = l > 0;
            }

            if (flag) {
                if (iinventory instanceof TileEntityHopper) {
                    ((TileEntityHopper) iinventory).c(8);
                    iinventory.update();
                }

                iinventory.update();
            }
        }

        return itemstack;
    }

    private IInventory z() {
        int i = BlockHopper.b(this.p());

        return getInventoryAt(this.getWorld(), (double) (this.x + Facing.b[i]), (double) (this.y + Facing.c[i]), (double) (this.z + Facing.d[i]));
    }

    public static IInventory getSourceInventory(IHopper ihopper) {
        return getInventoryAt(ihopper.getWorld(), ihopper.x(), ihopper.aD() + 1.0D, ihopper.aE());
    }

    public static EntityItem getEntityItemAt(World world, double d0, double d1, double d2) {
        List list = world.a(EntityItem.class, AxisAlignedBB.a(d0, d1, d2, d0 + 1.0D, d1 + 1.0D, d2 + 1.0D), IEntitySelector.a);

        return list.size() > 0 ? (EntityItem) list.get(0) : null;
    }

    public static IInventory getInventoryAt(World world, double d0, double d1, double d2) {
        IInventory iinventory = null;
        int i = MathHelper.floor(d0);
        int j = MathHelper.floor(d1);
        int k = MathHelper.floor(d2);
        TileEntity tileentity = world.getTileEntity(i, j, k);

        if (tileentity != null && tileentity instanceof IInventory) {
            iinventory = (IInventory) tileentity;
            if (iinventory instanceof TileEntityChest) {
                Block block = world.getType(i, j, k);

                if (block instanceof BlockChest) {
                    iinventory = ((BlockChest) block).m(world, i, j, k);
                }
            }
        }

        if (iinventory == null) {
            List list = world.getEntities((Entity) null, AxisAlignedBB.a(d0, d1, d2, d0 + 1.0D, d1 + 1.0D, d2 + 1.0D), IEntitySelector.c);

            if (list != null && list.size() > 0) {
                iinventory = (IInventory) list.get(world.random.nextInt(list.size()));
            }
        }

        return iinventory;
    }

    private static boolean canMergeItems(ItemStack itemstack, ItemStack itemstack1) {
        return itemstack.getItem() != itemstack1.getItem() ? false : (itemstack.getData() != itemstack1.getData() ? false : (itemstack.count > itemstack.getMaxStackSize() ? false : ItemStack.equals(itemstack, itemstack1)));
    }

    public double x() {
        return (double) this.x;
    }

    public double aD() {
        return (double) this.y;
    }

    public double aE() {
        return (double) this.z;
    }

    public void c(int i) {
        this.j = i;
    }

    public boolean j() {
        return this.j > 0;
    }
}
