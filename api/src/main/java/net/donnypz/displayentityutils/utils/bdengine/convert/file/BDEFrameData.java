package net.donnypz.displayentityutils.utils.bdengine.convert.file;

import org.joml.AxisAngle4f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class BDEFrameData implements Serializable {
    int time;
    Vector3f position, rotation, scale;

    @Serial
    private static final long serialVersionUID = 99L;

    public BDEFrameData(BDEFrameData data){
        this.position = new Vector3f(data.position);
        this.rotation = new Vector3f(data.rotation);
        this.scale = new Vector3f(data.scale);
    }

    BDEFrameData(Matrix4f matrix, Vector3f customPivot){
        scale = new Vector3f();
        matrix.getScale(scale);

        AxisAngle4f aa4f = new AxisAngle4f();
        matrix.getRotation(aa4f);
        Quaternionf q = new Quaternionf(aa4f);
        rotation = new Vector3f();
        q.getEulerAnglesXYZ(rotation);

        position = new Vector3f();
        matrix.getTranslation(position);
        if (customPivot != null) position.sub(customPivot);
    }

    BDEFrameData(Map<String, Object> map, Vector3f customPivot){
        //Time
        Number time = (Number) map.get("time");
        this.time = time == null ? -1 : time.intValue();

        //Position = (JSON: True Position + Custom Pivot, so reset by position-customPivot so any model can use this anim w/ its own custom pivot) |
        Object posObj = map.get("position");
        if (posObj instanceof Map pMap){
            position = getVector((Map<String, Object>) pMap);
            if (customPivot != null) position.sub(customPivot);
        }
        else if (posObj instanceof List pList){
            List<Number> pos = (List<Number>) pList;
            position = new Vector3f(pos.get(0).floatValue(), pos.get(1).floatValue(), pos.get(2).floatValue());
            if (customPivot != null) position.sub(customPivot);
        }
        else{
            position = new Vector3f();
        }

        //Rotation
        Map<String, Object> rot = (Map<String, Object>) map.get("rotation");
        rotation = rot == null ? new Vector3f() : getVector(rot);

        //Scale
        Object scaleObj = map.get("scale");
        if (scaleObj instanceof Map smap){
            scale = getVector((Map<String, Object>) smap);
        }
        else if (scaleObj instanceof List sList){
            List<Number> s = (List<Number>) sList;
            scale = new Vector3f(s.get(0).floatValue(), s.get(1).floatValue(), s.get(2).floatValue());
        }
        else{
            scale = new Vector3f(1);
        }


//        matrix4f = new Matrix4f()
//                .translate(position)
//                .rotate(new Quaternionf().rotationXYZ(rotation.x, rotation.y, rotation.z))
//                .scale(scale);
    }


    public Matrix4f getMatrix(){
        return new Matrix4f()
                .translate(position)
                .rotate(new Quaternionf().rotationXYZ(rotation.x, rotation.y, rotation.z))
                .scale(scale);
    }

    public Vector3f getPosition() {
        return new Vector3f(position);
    }

    public Vector3f getRotation() {
        return new Vector3f(rotation);
    }

    public Vector3f getScale() {
        return new Vector3f(scale);
    }

    private Vector3f getVector(Map<String, Object> map){
        return new Vector3f(getFloat(map, "x"), getFloat(map, "y"), getFloat(map, "z"));
    }

    private float getFloat(Map<String, Object> map, String key){
        return ((Number)map.get(key)).floatValue();
    }
}
