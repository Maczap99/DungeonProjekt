package ecs.components.skill;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import ecs.components.Component;
import ecs.components.MissingComponentException;
import ecs.components.PositionComponent;
import starter.Game;
import tools.Point;

import javax.swing.text.html.Option;
import java.util.Optional;

public class SkillTools {

    /**
     * calculates the last position in range regardless of aimed position
     *
     * @param startPoint position to start the calculation
     * @param aimPoint point to aim for
     * @param range range from start to
     * @return last position in range if you follow the directon from startPoint to aimPoint
     */
    public static Point calculateLastPositionInRange(
            Point startPoint, Point aimPoint, float range) {

        // calculate distance from startPoint to aimPoint
        float dx = aimPoint.x - startPoint.x;
        float dy = aimPoint.y - startPoint.y;

        // vector from startPoint to aimPoint
        Vector2 scv = new Vector2(dx, dy);

        // normalize the vector (length of 1)
        scv.nor();

        // resize the vector to the length of the range
        scv.scl(range);

        return new Point(startPoint.x + scv.x, startPoint.y + scv.y);
    }

    public static Point calculateVelocity(Point start, Point goal, float speed) {
        float x1 = start.x;
        float y1 = start.y;
        float x2 = goal.x;
        float y2 = goal.y;

        float dx = x2 - x1;
        float dy = y2 - y1;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);
        float velocityX = dx / distance * speed;
        float velocityY = dy / distance * speed;
        return new Point(velocityX, velocityY);
    }

    /**
     * gets the current cursor position as Point
     *
     * @return mouse cursor position as Point
     */
    public static Point getCursorPositionAsPoint() {
        Vector3 mousePosition =
                Game.camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
        return new Point(mousePosition.x, mousePosition.y);
    }


    /**
     * Uses the Player and Mouse Position get see
     * if mouse is placed north, east, south or west from player
     * @return north(0), east(1), south(2), west(3) dir as int
     */
    public static int getCursorPositionAsRelative4WayDirection(){
        Optional<Component> playerPC_OPT = Game.getHero().get().getComponent(PositionComponent.class);
        PositionComponent playerPC;

        if(playerPC_OPT.isPresent())
            playerPC = (PositionComponent) playerPC_OPT.get();
        else
            throw new MissingComponentException("The Player has no PositionComponent!");

        Point playerPoint = playerPC.getPosition();
        Point mousePos = getCursorPositionAsPoint();

        Point dir = Point.getUnitDirectionalVector(mousePos, playerPoint);

        if(Math.abs(dir.x) >= Math.abs(dir.y)){
            if(dir.x >= 0)
                return 1;//east
            else
                return 3;//west
        }
        else{
            if(dir.y >= 0)
                return 0;//north
            else
                return 2;//south
        }
    }
}
