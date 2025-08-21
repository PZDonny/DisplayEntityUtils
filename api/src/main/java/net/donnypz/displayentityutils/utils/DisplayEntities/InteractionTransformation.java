package net.donnypz.displayentityutils.utils.DisplayEntities;

import org.bukkit.util.Vector;
import org.jetbrains.annotations.ApiStatus;
import org.joml.Vector3f;

import java.io.*;

@ApiStatus.Internal
class InteractionTransformation extends Vector3f implements Externalizable {
    transient Vector vector;
    float height = -1;
    float width = -1;
    static final float invalidDirectionValue = 361;
    float groupYawAtCreation = invalidDirectionValue;
    float groupPitchAtCreation = invalidDirectionValue;

    @Serial
    private static final long serialVersionUID = 99L;

    @ApiStatus.Internal
    public InteractionTransformation(){}

    InteractionTransformation(Vector3f vector3f, float groupYawAtCreation, float groupPitchAtCreation, float height, float width){
       super(vector3f);
       this.vector = Vector.fromJOML(this);
       this.groupYawAtCreation = groupYawAtCreation;
       this.groupPitchAtCreation = groupPitchAtCreation;
       this.height = height;
       this.width = width;
    }

    @ApiStatus.Internal
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeFloat(this.x);
        out.writeFloat(this.y);
        out.writeFloat(this.z);

        out.writeFloat(groupYawAtCreation);
        out.writeFloat(groupPitchAtCreation);
        out.writeFloat(height);
        out.writeFloat(width);

    }

    @ApiStatus.Internal
    public void readExternal(ObjectInput in) throws IOException {
        this.x = in.readFloat();
        this.y = in.readFloat();
        this.z = in.readFloat();

        this.groupYawAtCreation = in.readFloat();
        this.groupPitchAtCreation = in.readFloat();
        this.height = in.readFloat();
        this.width = in.readFloat();

        this.vector = new Vector(x, y, z);
    }
}
