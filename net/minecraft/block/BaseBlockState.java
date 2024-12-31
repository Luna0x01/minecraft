package net.minecraft.block;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public interface BaseBlockState {
	Material getMaterial();

	boolean isFullBlock();

	boolean method_13361(Entity entity);

	int getOpacity();

	int getLuminance();

	boolean isTranslucent();

	boolean useNeighbourLight();

	MaterialColor getMaterialColor();

	BlockState withRotation(BlockRotation rotation);

	BlockState withMirror(BlockMirror mirror);

	boolean method_11730();

	BlockRenderType getRenderType();

	int method_11712(BlockView view, BlockPos pos);

	float getAmbientOcclusionLightLevel();

	boolean method_11733();

	boolean method_11734();

	boolean emitsRedstonePower();

	int getWeakRedstonePower(BlockView view, BlockPos pos, Direction direction);

	boolean method_11736();

	int getComparatorOutput(World world, BlockPos pos);

	float getHardness(World world, BlockPos pos);

	float method_11716(PlayerEntity player, World world, BlockPos pos);

	int getStrongRedstonePower(BlockView view, BlockPos pos, Direction direction);

	PistonBehavior getPistonBehavior();

	BlockState getBlockState(BlockView view, BlockPos pos);

	Box method_11722(World world, BlockPos pos);

	boolean method_11724(BlockView view, BlockPos pos, Direction direction);

	boolean isFullBoundsCubeForCulling();

	@Nullable
	Box getCollisionBox(World world, BlockPos pos);

	void addCollisionBoxesToList(World world, BlockPos pos, Box entityBox, List<Box> boxes, @Nullable Entity entity);

	Box getCollisionBox(BlockView view, BlockPos pos);

	BlockHitResult method_11711(World world, BlockPos pos, Vec3d vec3d, Vec3d vec3d2);

	boolean method_11739();
}
