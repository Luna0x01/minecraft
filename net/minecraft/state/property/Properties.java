package net.minecraft.state.property;

import java.util.function.Predicate;
import net.minecraft.block.enums.Attachment;
import net.minecraft.block.enums.BambooLeaves;
import net.minecraft.block.enums.BedPart;
import net.minecraft.block.enums.BlockHalf;
import net.minecraft.block.enums.ChestType;
import net.minecraft.block.enums.ComparatorMode;
import net.minecraft.block.enums.DoorHinge;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.block.enums.Instrument;
import net.minecraft.block.enums.JigsawOrientation;
import net.minecraft.block.enums.PistonType;
import net.minecraft.block.enums.RailShape;
import net.minecraft.block.enums.SculkSensorPhase;
import net.minecraft.block.enums.SlabType;
import net.minecraft.block.enums.StairShape;
import net.minecraft.block.enums.StructureBlockMode;
import net.minecraft.block.enums.Thickness;
import net.minecraft.block.enums.Tilt;
import net.minecraft.block.enums.WallMountLocation;
import net.minecraft.block.enums.WallShape;
import net.minecraft.block.enums.WireConnection;
import net.minecraft.util.math.Direction;

public class Properties {
	public static final BooleanProperty ATTACHED = BooleanProperty.of("attached");
	public static final BooleanProperty BOTTOM = BooleanProperty.of("bottom");
	public static final BooleanProperty CONDITIONAL = BooleanProperty.of("conditional");
	public static final BooleanProperty DISARMED = BooleanProperty.of("disarmed");
	public static final BooleanProperty DRAG = BooleanProperty.of("drag");
	public static final BooleanProperty ENABLED = BooleanProperty.of("enabled");
	public static final BooleanProperty EXTENDED = BooleanProperty.of("extended");
	public static final BooleanProperty EYE = BooleanProperty.of("eye");
	public static final BooleanProperty FALLING = BooleanProperty.of("falling");
	public static final BooleanProperty HANGING = BooleanProperty.of("hanging");
	public static final BooleanProperty HAS_BOTTLE_0 = BooleanProperty.of("has_bottle_0");
	public static final BooleanProperty HAS_BOTTLE_1 = BooleanProperty.of("has_bottle_1");
	public static final BooleanProperty HAS_BOTTLE_2 = BooleanProperty.of("has_bottle_2");
	public static final BooleanProperty HAS_RECORD = BooleanProperty.of("has_record");
	public static final BooleanProperty HAS_BOOK = BooleanProperty.of("has_book");
	public static final BooleanProperty INVERTED = BooleanProperty.of("inverted");
	public static final BooleanProperty IN_WALL = BooleanProperty.of("in_wall");
	public static final BooleanProperty LIT = BooleanProperty.of("lit");
	public static final BooleanProperty LOCKED = BooleanProperty.of("locked");
	public static final BooleanProperty OCCUPIED = BooleanProperty.of("occupied");
	public static final BooleanProperty OPEN = BooleanProperty.of("open");
	public static final BooleanProperty PERSISTENT = BooleanProperty.of("persistent");
	public static final BooleanProperty POWERED = BooleanProperty.of("powered");
	public static final BooleanProperty SHORT = BooleanProperty.of("short");
	public static final BooleanProperty SIGNAL_FIRE = BooleanProperty.of("signal_fire");
	public static final BooleanProperty SNOWY = BooleanProperty.of("snowy");
	public static final BooleanProperty TRIGGERED = BooleanProperty.of("triggered");
	public static final BooleanProperty UNSTABLE = BooleanProperty.of("unstable");
	public static final BooleanProperty WATERLOGGED = BooleanProperty.of("waterlogged");
	public static final BooleanProperty VINE_END = BooleanProperty.of("vine_end");
	public static final BooleanProperty BERRIES = BooleanProperty.of("berries");
	public static final EnumProperty<Direction.Axis> HORIZONTAL_AXIS = EnumProperty.of("axis", Direction.Axis.class, Direction.Axis.X, Direction.Axis.Z);
	public static final EnumProperty<Direction.Axis> AXIS = EnumProperty.of("axis", Direction.Axis.class);
	public static final BooleanProperty UP = BooleanProperty.of("up");
	public static final BooleanProperty DOWN = BooleanProperty.of("down");
	public static final BooleanProperty NORTH = BooleanProperty.of("north");
	public static final BooleanProperty EAST = BooleanProperty.of("east");
	public static final BooleanProperty SOUTH = BooleanProperty.of("south");
	public static final BooleanProperty WEST = BooleanProperty.of("west");
	public static final DirectionProperty FACING = DirectionProperty.of(
		"facing", Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST, Direction.UP, Direction.DOWN
	);
	public static final DirectionProperty HOPPER_FACING = DirectionProperty.of("facing", (Predicate<Direction>)(facing -> facing != Direction.UP));
	public static final DirectionProperty HORIZONTAL_FACING = DirectionProperty.of("facing", Direction.Type.HORIZONTAL);
	public static final EnumProperty<JigsawOrientation> ORIENTATION = EnumProperty.of("orientation", JigsawOrientation.class);
	public static final EnumProperty<WallMountLocation> WALL_MOUNT_LOCATION = EnumProperty.of("face", WallMountLocation.class);
	public static final EnumProperty<Attachment> ATTACHMENT = EnumProperty.of("attachment", Attachment.class);
	public static final EnumProperty<WallShape> EAST_WALL_SHAPE = EnumProperty.of("east", WallShape.class);
	public static final EnumProperty<WallShape> NORTH_WALL_SHAPE = EnumProperty.of("north", WallShape.class);
	public static final EnumProperty<WallShape> SOUTH_WALL_SHAPE = EnumProperty.of("south", WallShape.class);
	public static final EnumProperty<WallShape> WEST_WALL_SHAPE = EnumProperty.of("west", WallShape.class);
	public static final EnumProperty<WireConnection> EAST_WIRE_CONNECTION = EnumProperty.of("east", WireConnection.class);
	public static final EnumProperty<WireConnection> NORTH_WIRE_CONNECTION = EnumProperty.of("north", WireConnection.class);
	public static final EnumProperty<WireConnection> SOUTH_WIRE_CONNECTION = EnumProperty.of("south", WireConnection.class);
	public static final EnumProperty<WireConnection> WEST_WIRE_CONNECTION = EnumProperty.of("west", WireConnection.class);
	public static final EnumProperty<DoubleBlockHalf> DOUBLE_BLOCK_HALF = EnumProperty.of("half", DoubleBlockHalf.class);
	public static final EnumProperty<BlockHalf> BLOCK_HALF = EnumProperty.of("half", BlockHalf.class);
	public static final EnumProperty<RailShape> RAIL_SHAPE = EnumProperty.of("shape", RailShape.class);
	public static final EnumProperty<RailShape> STRAIGHT_RAIL_SHAPE = EnumProperty.of(
		"shape",
		RailShape.class,
		(Predicate)(shape -> shape != RailShape.NORTH_EAST && shape != RailShape.NORTH_WEST && shape != RailShape.SOUTH_EAST && shape != RailShape.SOUTH_WEST)
	);
	public static final int AGE_1_MAX = 1;
	public static final int AGE_2_MAX = 2;
	public static final int AGE_3_MAX = 3;
	public static final int AGE_5_MAX = 5;
	public static final int AGE_7_MAX = 7;
	public static final int AGE_15_MAX = 15;
	public static final int AGE_25_MAX = 25;
	public static final IntProperty AGE_1 = IntProperty.of("age", 0, 1);
	public static final IntProperty AGE_2 = IntProperty.of("age", 0, 2);
	public static final IntProperty AGE_3 = IntProperty.of("age", 0, 3);
	public static final IntProperty AGE_5 = IntProperty.of("age", 0, 5);
	public static final IntProperty AGE_7 = IntProperty.of("age", 0, 7);
	public static final IntProperty AGE_15 = IntProperty.of("age", 0, 15);
	public static final IntProperty AGE_25 = IntProperty.of("age", 0, 25);
	public static final IntProperty BITES = IntProperty.of("bites", 0, 6);
	public static final IntProperty CANDLES = IntProperty.of("candles", 1, 4);
	public static final IntProperty DELAY = IntProperty.of("delay", 1, 4);
	public static final int DISTANCE_1_7_MAX = 7;
	public static final IntProperty DISTANCE_1_7 = IntProperty.of("distance", 1, 7);
	public static final IntProperty EGGS = IntProperty.of("eggs", 1, 4);
	public static final IntProperty HATCH = IntProperty.of("hatch", 0, 2);
	public static final IntProperty LAYERS = IntProperty.of("layers", 1, 8);
	public static final int LEVEL_3_MIN = 0;
	public static final int LEVEL_1_8_MIN = 1;
	public static final int LEVEL_3_MAX = 3;
	public static final int LEVEL_1_8_MAX = 8;
	public static final IntProperty LEVEL_3 = IntProperty.of("level", 1, 3);
	public static final IntProperty LEVEL_8 = IntProperty.of("level", 0, 8);
	public static final IntProperty LEVEL_1_8 = IntProperty.of("level", 1, 8);
	public static final IntProperty HONEY_LEVEL = IntProperty.of("honey_level", 0, 5);
	public static final int LEVEL_15_MAX = 15;
	public static final IntProperty LEVEL_15 = IntProperty.of("level", 0, 15);
	public static final IntProperty MOISTURE = IntProperty.of("moisture", 0, 7);
	public static final IntProperty NOTE = IntProperty.of("note", 0, 24);
	public static final IntProperty PICKLES = IntProperty.of("pickles", 1, 4);
	public static final IntProperty POWER = IntProperty.of("power", 0, 15);
	public static final IntProperty STAGE = IntProperty.of("stage", 0, 1);
	public static final int DISTANCE_0_7_MAX = 7;
	public static final IntProperty DISTANCE_0_7 = IntProperty.of("distance", 0, 7);
	public static final int CHARGES_MIN = 0;
	public static final int CHARGES_MAX = 4;
	public static final IntProperty CHARGES = IntProperty.of("charges", 0, 4);
	public static final int ROTATION_MAX = 15;
	public static final IntProperty ROTATION = IntProperty.of("rotation", 0, 15);
	public static final EnumProperty<BedPart> BED_PART = EnumProperty.of("part", BedPart.class);
	public static final EnumProperty<ChestType> CHEST_TYPE = EnumProperty.of("type", ChestType.class);
	public static final EnumProperty<ComparatorMode> COMPARATOR_MODE = EnumProperty.of("mode", ComparatorMode.class);
	public static final EnumProperty<DoorHinge> DOOR_HINGE = EnumProperty.of("hinge", DoorHinge.class);
	public static final EnumProperty<Instrument> INSTRUMENT = EnumProperty.of("instrument", Instrument.class);
	public static final EnumProperty<PistonType> PISTON_TYPE = EnumProperty.of("type", PistonType.class);
	public static final EnumProperty<SlabType> SLAB_TYPE = EnumProperty.of("type", SlabType.class);
	public static final EnumProperty<StairShape> STAIR_SHAPE = EnumProperty.of("shape", StairShape.class);
	public static final EnumProperty<StructureBlockMode> STRUCTURE_BLOCK_MODE = EnumProperty.of("mode", StructureBlockMode.class);
	public static final EnumProperty<BambooLeaves> BAMBOO_LEAVES = EnumProperty.of("leaves", BambooLeaves.class);
	public static final EnumProperty<Tilt> TILT = EnumProperty.of("tilt", Tilt.class);
	public static final DirectionProperty VERTICAL_DIRECTION = DirectionProperty.of("vertical_direction", Direction.UP, Direction.DOWN);
	public static final EnumProperty<Thickness> THICKNESS = EnumProperty.of("thickness", Thickness.class);
	public static final EnumProperty<SculkSensorPhase> SCULK_SENSOR_PHASE = EnumProperty.of("sculk_sensor_phase", SculkSensorPhase.class);
}
